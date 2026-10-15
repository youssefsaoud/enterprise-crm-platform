import { AfterViewInit, Component, ElementRef, EventEmitter, Input, OnChanges, OnDestroy, Output, SimpleChanges, ViewChild } from '@angular/core';
import { BarChart, LineChart, PieChart } from 'echarts/charts';
import { GridComponent, LegendComponent, TooltipComponent } from 'echarts/components';
import { init, use } from 'echarts/core';
import type { ECharts } from 'echarts/core';
import type { EChartsOption } from 'echarts';
import { CanvasRenderer } from 'echarts/renderers';

use([BarChart, LineChart, PieChart, GridComponent, LegendComponent, TooltipComponent, CanvasRenderer]);

export interface DashboardChartSelection {
  name?: string;
  data?: unknown;
}

@Component({
  selector: 'app-dashboard-chart',
  standalone: true,
  template: '<div #host class="chart-host" role="img" [attr.aria-label]="ariaLabel"></div>',
  styles: [':host{display:block;min-width:0}.chart-host{width:100%;height:20rem;min-height:16rem}']
})
export class DashboardChartComponent implements AfterViewInit, OnChanges, OnDestroy {
  @ViewChild('host', { static: true }) private host!: ElementRef<HTMLDivElement>;
  @Input() option: EChartsOption = {};
  @Input() ariaLabel = 'Dashboard chart';
  @Output() readonly pointSelected = new EventEmitter<DashboardChartSelection>();

  private chart?: ECharts;
  private resizeObserver?: ResizeObserver;
  ngAfterViewInit(): void {
    this.chart = init(this.host.nativeElement, undefined, { renderer: 'canvas' });
    this.chart.setOption(this.option);
    this.chart.on('click', event => this.pointSelected.emit({ name: event.name, data: event.data }));
    this.resizeObserver = new ResizeObserver(() => this.chart?.resize());
    this.resizeObserver.observe(this.host.nativeElement);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['option'] && this.chart) this.chart.setOption(this.option, true);
  }

  ngOnDestroy(): void {
    this.resizeObserver?.disconnect();
    this.chart?.dispose();
  }
}
