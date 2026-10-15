import type {
  Group,
  Mesh,
  PerspectiveCamera,
  PlaneGeometry,
  Scene,
  ShaderMaterial,
  WebGLRenderer
} from 'three';

type ThreeModule = typeof import('three');

interface FlowLayer {
  mesh: Mesh<PlaneGeometry, ShaderMaterial>;
  material: ShaderMaterial;
  baseX: number;
  baseY: number;
  baseZ: number;
  baseRotation: number;
  drift: number;
  parallax: number;
}

const vertexShader = `
  uniform float uTime;
  uniform float uPhase;
  uniform float uAmplitude;
  uniform vec2 uPointer;
  varying vec2 vUv;
  varying float vLift;

  void main() {
    vUv = uv;
    vec3 transformed = position;
    float envelope = sin(uv.x * 3.14159265);
    float primaryWave = sin(position.x * 0.72 + uPhase + uTime * 0.24);
    float secondaryWave = sin(position.x * 1.48 - uTime * 0.17 + position.y * 0.58);
    float quietDetail = cos(position.x * 2.1 + uPhase - uTime * 0.09) * 0.12;
    vec2 pointerUv = vec2(0.5 + uPointer.x * 0.34, 0.5 - uPointer.y * 0.3);
    float pointerField = 1.0 - smoothstep(0.08, 0.72, distance(uv, pointerUv));
    float pointerEnergy = length(uPointer);

    transformed.z += (primaryWave * 0.68 + secondaryWave * 0.24 + quietDetail) * uAmplitude * envelope;
    transformed.y += sin(position.x * 0.46 + uPhase + uTime * 0.16) * 0.42 * uAmplitude;
    transformed.x += uPointer.x * (uv.y - 0.5) * 0.32;
    transformed.y += uPointer.y * pointerField * 0.24;
    transformed.z += pointerField * pointerEnergy * 0.42 + uPointer.y * envelope * 0.24;
    vLift = transformed.z;

    gl_Position = projectionMatrix * modelViewMatrix * vec4(transformed, 1.0);
  }
`;

const fragmentShader = `
  uniform float uTime;
  uniform float uPhase;
  uniform float uOpacity;
  uniform vec2 uPointer;
  varying vec2 vUv;
  varying float vLift;

  void main() {
    float lowerEdge = smoothstep(0.0, 0.18, vUv.y);
    float upperEdge = 1.0 - smoothstep(0.82, 1.0, vUv.y);
    float longitudinalFade = smoothstep(0.0, 0.12, vUv.x) * (1.0 - smoothstep(0.88, 1.0, vUv.x));
    float edgeFade = lowerEdge * upperEdge * longitudinalFade;
    float movingReflection = pow(max(0.0, 0.5 + 0.5 * sin(vUv.x * 12.0 - uTime * 0.34 + uPhase + vUv.y * 3.0)), 8.0);
    float glassLine = pow(max(0.0, 1.0 - abs(vUv.y - 0.67) * 5.0), 8.0);
    vec2 pointerUv = vec2(0.5 + uPointer.x * 0.34, 0.5 - uPointer.y * 0.3);
    float pointerLight = max(0.0, 1.0 - distance(vUv, pointerUv) * 1.52);
    float pointerGlow = pow(pointerLight, 2.0);

    vec3 midnight = vec3(0.006, 0.025, 0.085);
    vec3 electricBlue = vec3(0.035, 0.36, 1.0);
    vec3 iceBlue = vec3(0.05, 0.82, 1.0);
    float colorMix = clamp(vUv.y * 0.78 + vLift * 0.1 + movingReflection * 0.24, 0.0, 1.0);
    vec3 color = mix(midnight, electricBlue, colorMix);
    color = mix(color, iceBlue, glassLine * 0.48 + pointerGlow * 0.72);
    color += vec3(0.02, 0.18, 0.42) * pointerGlow * 0.48;
    float alpha = edgeFade * uOpacity * (0.46 + movingReflection * 0.25 + glassLine * 0.18 + pointerGlow * 0.48);

    gl_FragColor = vec4(color, alpha);
  }
`;

export class OrchestrationScene {
  private three?: ThreeModule;
  private renderer?: WebGLRenderer;
  private scene?: Scene;
  private camera?: PerspectiveCamera;
  private flowGroup?: Group;
  private layers: FlowLayer[] = [];
  private frameId: number | null = null;
  private resizeObserver?: ResizeObserver;
  private destroyed = false;
  private running = false;
  private contextLost = false;
  private startTime = performance.now();
  private previousFrame = 0;
  private pointerTarget = { x: 0, y: 0 };
  private pointerCurrent = { x: 0, y: 0 };
  private readonly lowPower: boolean;

  constructor(
    private readonly canvas: HTMLCanvasElement,
    private readonly pointerSurface: HTMLElement
  ) {
    const memory = (navigator as Navigator & { deviceMemory?: number }).deviceMemory ?? 8;
    this.lowPower = navigator.hardwareConcurrency <= 4 || memory <= 4;
  }

  async start(): Promise<void> {
    if (this.destroyed || this.shouldUseFallback() || !this.supportsWebGl()) {
      this.setState('fallback');
      return;
    }

    try {
      this.setState('loading');
      this.three = await import('three');
      if (this.destroyed) return;
      this.initializeScene(this.three);
      this.bindEvents();
      this.setState('ready');
      this.running = true;
      this.frameId = requestAnimationFrame(this.render);
    } catch {
      this.setState('fallback');
      this.disposeScene();
    }
  }

  destroy(): void {
    this.destroyed = true;
    this.running = false;
    if (this.frameId !== null) cancelAnimationFrame(this.frameId);
    this.frameId = null;
    this.resizeObserver?.disconnect();
    document.removeEventListener('visibilitychange', this.onVisibilityChange);
    this.pointerSurface.removeEventListener('pointermove', this.onPointerMove);
    this.pointerSurface.removeEventListener('pointerleave', this.onPointerLeave);
    this.canvas.removeEventListener('webglcontextlost', this.onContextLost);
    this.canvas.removeEventListener('webglcontextrestored', this.onContextRestored);
    this.disposeScene();
  }

  private initializeScene(three: ThreeModule): void {
    const width = Math.max(1, this.pointerSurface.clientWidth);
    const height = Math.max(1, this.pointerSurface.clientHeight);
    const renderer = new three.WebGLRenderer({
      canvas: this.canvas,
      alpha: true,
      antialias: !this.lowPower,
      powerPreference: this.lowPower ? 'low-power' : 'high-performance',
      premultipliedAlpha: true
    });
    renderer.setPixelRatio(Math.min(window.devicePixelRatio || 1, this.lowPower ? 1 : 1.5));
    renderer.setSize(width, height, false);
    renderer.outputColorSpace = three.SRGBColorSpace;
    renderer.toneMapping = three.ACESFilmicToneMapping;
    renderer.toneMappingExposure = 1.05;

    this.renderer = renderer;
    this.scene = new three.Scene();
    this.camera = new three.PerspectiveCamera(38, width / height, 0.1, 50);
    this.camera.position.set(0, 0.1, 8.6);

    this.flowGroup = new three.Group();
    this.flowGroup.position.set(-0.35, -0.15, 0);
    this.scene.add(this.flowGroup);

    const layerSettings = [
      { y: 2.35, z: -1.25, rotation: -0.24, scale: 1.35, phase: 0.4, amplitude: 0.82, opacity: 0.34, drift: 0.12 },
      { y: 0.85, z: -0.25, rotation: 0.08, scale: 1.18, phase: 2.1, amplitude: 1.08, opacity: 0.56, drift: -0.16 },
      { y: -1.0, z: 0.45, rotation: -0.14, scale: 1.28, phase: 4.3, amplitude: 0.92, opacity: 0.68, drift: 0.14 },
      { y: -2.75, z: -0.65, rotation: 0.18, scale: 1.42, phase: 5.6, amplitude: 0.72, opacity: 0.3, drift: -0.1 }
    ];

    this.layers = layerSettings.map((settings, index) => {
      const geometry = new three.PlaneGeometry(10.5, 2.45, this.lowPower ? 44 : 84, this.lowPower ? 12 : 22);
      const material = new three.ShaderMaterial({
        vertexShader,
        fragmentShader,
        transparent: true,
        depthWrite: false,
        side: three.DoubleSide,
        blending: index === 1 || index === 2 ? three.AdditiveBlending : three.NormalBlending,
        uniforms: {
          uTime: { value: 0 },
          uPhase: { value: settings.phase },
          uAmplitude: { value: settings.amplitude },
          uOpacity: { value: settings.opacity },
          uPointer: { value: new three.Vector2() }
        }
      });
      const mesh = new three.Mesh(geometry, material);
      const baseX = index % 2 === 0 ? -0.65 : 0.65;
      mesh.position.set(baseX, settings.y, settings.z);
      mesh.rotation.set(-0.08 + index * 0.025, index % 2 === 0 ? -0.08 : 0.06, settings.rotation);
      mesh.scale.setScalar(settings.scale);
      this.flowGroup?.add(mesh);
      return {
        mesh,
        material,
        baseX,
        baseY: settings.y,
        baseZ: settings.z,
        baseRotation: settings.rotation,
        drift: settings.drift,
        parallax: 0.18 + index * 0.075
      };
    });

    this.resizeObserver = new ResizeObserver(this.resize);
    this.resizeObserver.observe(this.pointerSurface);
  }

  private bindEvents(): void {
    document.addEventListener('visibilitychange', this.onVisibilityChange);
    this.pointerSurface.addEventListener('pointermove', this.onPointerMove, { passive: true });
    this.pointerSurface.addEventListener('pointerleave', this.onPointerLeave);
    this.canvas.addEventListener('webglcontextlost', this.onContextLost);
    this.canvas.addEventListener('webglcontextrestored', this.onContextRestored);
  }

  private readonly render = (timestamp: number): void => {
    if (!this.running || this.destroyed || this.contextLost || !this.renderer || !this.scene || !this.camera || !this.flowGroup) return;
    const minimumInterval = this.lowPower ? 1000 / 30 : 0;
    if (timestamp - this.previousFrame < minimumInterval) {
      this.frameId = requestAnimationFrame(this.render);
      return;
    }
    this.previousFrame = timestamp;

    this.pointerCurrent.x += (this.pointerTarget.x - this.pointerCurrent.x) * 0.11;
    this.pointerCurrent.y += (this.pointerTarget.y - this.pointerCurrent.y) * 0.11;

    const elapsed = (timestamp - this.startTime) / 1000;
    this.camera.position.x = this.pointerCurrent.x * 0.72;
    this.camera.position.y = 0.1 - this.pointerCurrent.y * 0.48;
    this.camera.lookAt(0, 0, 0);
    this.flowGroup.position.x = -0.35 + this.pointerCurrent.x * 0.28;
    this.flowGroup.position.y = -0.15 - this.pointerCurrent.y * 0.2;
    this.flowGroup.rotation.y = this.pointerCurrent.x * 0.14;
    this.flowGroup.rotation.x = this.pointerCurrent.y * 0.1;

    this.layers.forEach((layer, index) => {
      layer.material.uniforms['uTime'].value = elapsed;
      layer.material.uniforms['uPointer'].value.set(this.pointerCurrent.x, this.pointerCurrent.y);
      layer.mesh.rotation.z = layer.baseRotation + Math.sin(elapsed * 0.11 + index) * 0.018;
      layer.mesh.position.x = layer.baseX + layer.drift * Math.sin(elapsed * 0.15 + index)
        + this.pointerCurrent.x * layer.parallax;
      layer.mesh.position.y = layer.baseY - this.pointerCurrent.y * layer.parallax * 0.52;
      layer.mesh.position.z = layer.baseZ + this.pointerCurrent.x * layer.parallax * 0.18;
    });

    this.renderer.render(this.scene, this.camera);
    this.frameId = requestAnimationFrame(this.render);
  };

  private readonly resize = (): void => {
    if (!this.renderer || !this.camera) return;
    const width = Math.max(1, this.pointerSurface.clientWidth);
    const height = Math.max(1, this.pointerSurface.clientHeight);
    this.camera.aspect = width / height;
    this.camera.updateProjectionMatrix();
    this.renderer.setPixelRatio(Math.min(window.devicePixelRatio || 1, this.lowPower ? 1 : 1.5));
    this.renderer.setSize(width, height, false);
  };

  private readonly onPointerMove = (event: PointerEvent): void => {
    if (event.pointerType === 'touch') return;
    const bounds = this.pointerSurface.getBoundingClientRect();
    this.pointerTarget.x = ((event.clientX - bounds.left) / bounds.width - 0.5) * 2;
    this.pointerTarget.y = ((event.clientY - bounds.top) / bounds.height - 0.5) * 2;
  };

  private readonly onPointerLeave = (): void => {
    this.pointerTarget.x = 0;
    this.pointerTarget.y = 0;
  };

  private readonly onVisibilityChange = (): void => {
    if (document.hidden) {
      this.running = false;
      if (this.frameId !== null) cancelAnimationFrame(this.frameId);
      this.frameId = null;
      return;
    }
    if (!this.destroyed && !this.contextLost && this.renderer) {
      this.startTime = performance.now();
      this.previousFrame = 0;
      this.running = true;
      this.frameId = requestAnimationFrame(this.render);
    }
  };

  private readonly onContextLost = (event: Event): void => {
    event.preventDefault();
    this.contextLost = true;
    this.running = false;
    if (this.frameId !== null) cancelAnimationFrame(this.frameId);
    this.frameId = null;
    this.setState('fallback');
  };

  private readonly onContextRestored = (): void => {
    this.contextLost = false;
    this.setState('ready');
    this.resize();
    if (!document.hidden && !this.destroyed) {
      this.startTime = performance.now();
      this.running = true;
      this.frameId = requestAnimationFrame(this.render);
    }
  };

  private shouldUseFallback(): boolean {
    return window.matchMedia('(prefers-reduced-motion: reduce)').matches || window.matchMedia('(pointer: coarse)').matches;
  }

  private supportsWebGl(): boolean {
    try {
      const probe = document.createElement('canvas');
      return Boolean(probe.getContext('webgl2') || probe.getContext('webgl'));
    } catch {
      return false;
    }
  }

  private setState(state: 'loading' | 'ready' | 'fallback'): void {
    this.pointerSurface.dataset['sceneState'] = state;
  }

  private disposeScene(): void {
    this.layers.forEach(({ mesh, material }) => {
      mesh.geometry.dispose();
      material.dispose();
      mesh.removeFromParent();
    });
    this.layers = [];
    this.renderer?.dispose();
    this.renderer = undefined;
    this.scene = undefined;
    this.camera = undefined;
    this.flowGroup = undefined;
  }
}
