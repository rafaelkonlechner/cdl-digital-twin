<template>
<div :id="chartId" style="text-align: left;" v-bind:style="{'width': width + 'px', 'height': height + 'px'}"></div>
</template>
<script>
import * as Plottable from "plottable";
import "plottable/plottable.css";
export default {
    name: "line-chart",
    props: ["width", "height", "data", "min", "max"],
    data() {
        return {
            chartId: Math.random().toString(36).substring(2, 15),
            chart: null,
            xScale: null,
            yScale: null,
            xAxis: null,
            yAxis: null,
            plot: null,
            dataSet: null
        };
    },
    watch: {
        data: function(newData) {
            this.yScale.domainMin(this.min);
            this.yScale.domainMax(this.max);
            this.plot.removeDataset(this.dataset);
            this.dataset = new Plottable.Dataset(newData);
            this.plot.addDataset(this.dataset);
            this.plot.redraw();
        }
    },
    created() {
        this.init();
    },
    mounted() {
        this.redraw();
    },
    methods: {
        init() {
            this.xScale = new Plottable.Scales.Linear();
            this.yScale = new Plottable.Scales.Linear();
            this.yScale.domainMin(this.min);
            this.yScale.domainMax(this.max);

            this.xAxis = new Plottable.Axes.Numeric(this.xScale, "bottom").xAlignment(
                "center"
            );
            this.yAxis = new Plottable.Axes.Numeric(this.yScale, "left").yAlignment(
                "center"
            );

            this.plot = new Plottable.Plots.Line();
            this.plot.x(function(d) {
                return d.x;
            }, this.xScale);
            this.plot.y(function(d) {
                return d.y;
            }, this.yScale);
            this.plot.attr("stroke", "#ffffff");
            this.dataset = new Plottable.Dataset(this.data);
            this.plot.addDataset(this.dataset);
            this.chart = new Plottable.Components.Table([
                [this.yAxis, this.plot],
                [null, this.xAxis]
            ]);
        },
        redraw() {
            this.chart.renderTo("#" + this.chartId);
        },
        addDataPoint(p) {
            this.data.push(p);
            this.chart.renderTo("#" + this.chartId);
        }
    }
};
</script>
