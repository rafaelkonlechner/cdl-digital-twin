<template>
  <div :id="id" style="text-align: left;" v-bind:style="{'width': width, 'height': height}"></div>
</template>

<script>
import * as Plottable from "plottable";
import "plottable/plottable.css";
export default {
  name: "line-chart2",
  props: ["id", "width", "height"],
  mounted() {
    this.init();
  },
  methods: {
    generateData() {
      var data = [
        {
          date: 0, //new Date(2018, 2, 31, 13, 0),
          value: 50
        }
      ];
      for (var i = 2; i <= 365; i++) {
        data.push({
          date: i, //new Date(2018, 2, 31, 13, i),
          value: data[data.length - 1].value + Math.random() * 2 - 1
        });
      }
      return data;
    },
    generatePlotGroup(xScale, yScale) {
      var linePlot = new Plottable.Plots.Line()
        .addDataset(new Plottable.Dataset(this.generateData()))
        .x(function(d) {
          return d.date;
        }, xScale)
        .y(function(d) {
          return d.value;
        }, yScale)
        .attr("opacity", 0.9);

      var datasetForFocusPoint = new Plottable.Dataset();
      var datasetForFocusPoint2 = new Plottable.Dataset();

      var selectedPoint = new Plottable.Plots.Scatter()
        .x(function(d) {
          return d.date;
        }, xScale)
        .y(function(d) {
          return d.value;
        }, yScale)
        .size(10)
        .attr("opacity", 1)
        .addDataset(datasetForFocusPoint);

      var selectedPointHighlight = new Plottable.Plots.Scatter()
        .x(function(d) {
          return d.date;
        }, xScale)
        .y(function(d) {
          return d.value;
        }, yScale)
        .size(20)
        .attr("opacity", 0.25)
        .addDataset(datasetForFocusPoint);

      var selectedPoint2 = new Plottable.Plots.Scatter()
        .x(function(d) {
          return d.date;
        }, xScale)
        .y(function(d) {
          return d.value;
        }, yScale)
        .attr("fill", "#F99D42")
        .size(10)
        .attr("opacity", 1)
        .addDataset(datasetForFocusPoint2);

      var guideline = new Plottable.Components.GuideLineLayer(
        Plottable.Components.GuideLineLayer.ORIENTATION_VERTICAL
      ).scale(xScale);

      var guideline2 = new Plottable.Components.GuideLineLayer(
        Plottable.Components.GuideLineLayer.ORIENTATION_VERTICAL
      ).scale(xScale);

      return new Plottable.Components.Group([
        linePlot,
        guideline,
        guideline2,
        selectedPoint,
        selectedPointHighlight,
        selectedPoint2
      ]);
    },
    generateInteraction(plotGroup1, plotGroup2) {
      var linePlot1 = plotGroup1.components()[0];
      var linePlot2 = plotGroup2.components()[0];

      var guideline1 = plotGroup1.components()[1];
      var guideline2 = plotGroup2.components()[1];

      var selectedPoint1 = plotGroup1.components()[3];
      var selectedPoint2 = plotGroup2.components()[3];

      var interaction = new Plottable.Interactions.Pointer();
      interaction.onPointerMove(function(point) {
        var nearestEntityByX = linePlot1.entityNearestByXThenY(point);
        var otherNearestEntityByX = linePlot2.entityNearestByXThenY(point);
        selectedPoint1.datasets()[0].data([nearestEntityByX.datum]);
        selectedPoint2.datasets()[0].data([otherNearestEntityByX.datum]);
        guideline1.value(nearestEntityByX.datum.date);
        guideline2.value(otherNearestEntityByX.datum.date);
      });
      return interaction;
    },
    generateInteractionClick(plotGroup1, plotGroup2) {
      var linePlot1 = plotGroup1.components()[0];
      var linePlot2 = plotGroup2.components()[0];

      var guideline1 = plotGroup1.components()[2];
      var guideline2 = plotGroup2.components()[2];

      var selectedPoint1 = plotGroup1.components()[5];
      var selectedPoint2 = plotGroup2.components()[5];

      var interaction = new Plottable.Interactions.Click();
      interaction.onClick(function(point) {
        console.log(point);
        var nearestEntityByX = linePlot1.entityNearestByXThenY(point);
        var otherNearestEntityByX = linePlot2.entityNearestByXThenY(point);
        console.log(nearestEntityByX);
        console.log(otherNearestEntityByX);
        selectedPoint1.datasets()[0].data([nearestEntityByX.datum]);
        selectedPoint2.datasets()[0].data([otherNearestEntityByX.datum]);
        guideline1.value(nearestEntityByX.datum.date);
        guideline2.value(otherNearestEntityByX.datum.date);
      });
      return interaction;
    },
    init() {
      var xScale = new Plottable.Scales.Linear();
      var yScaleTop = new Plottable.Scales.Linear();
      var yScaleBottom = new Plottable.Scales.Linear();

      var plotGroupTop = this.generatePlotGroup(xScale, yScaleTop);
      var plotGroupBottom = this.generatePlotGroup(xScale, yScaleBottom);

      this.generateInteraction(plotGroupTop, plotGroupBottom).attachTo(
        plotGroupTop.components()[0]
      );
      this.generateInteraction(plotGroupBottom, plotGroupTop).attachTo(
        plotGroupBottom.components()[0]
      );

      this.generateInteractionClick(plotGroupTop, plotGroupBottom).attachTo(
        plotGroupTop.components()[0]
      );
      this.generateInteractionClick(plotGroupBottom, plotGroupTop).attachTo(
        plotGroupBottom.components()[0]
      );

      var xAxisTop = new Plottable.Axes.Numeric(xScale, "bottom");
      var yAxisTop = new Plottable.Axes.Numeric(yScaleTop, "left");

      var xAxisBottom = new Plottable.Axes.Numeric(xScale, "bottom");
      var yAxisBottom = new Plottable.Axes.Numeric(yScaleBottom, "left");

      var chart1 = new Plottable.Components.Table([
        [yAxisTop, plotGroupTop],
        [null, xAxisTop]
      ]);

      var chart2 = new Plottable.Components.Table([
        [yAxisBottom, plotGroupBottom],
        [null, xAxisBottom]
      ]);

      var table = new Plottable.Components.Table([[chart1], [chart2]]);
      table.renderTo("div#" + this.id);
    }
  }
};
</script>