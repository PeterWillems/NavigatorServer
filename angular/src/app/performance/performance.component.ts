import {Component, Input, OnInit} from '@angular/core';
import {SeObjectModel} from '../models/se-object.model';
import {Dataset} from '../dataset/dataset.model';
import {DatasetService} from '../dataset.service';
import {PerformanceModel} from '../models/performance.model';
import {PerformanceService} from '../performance.service';

@Component({
  selector: 'app-performance',
  templateUrl: './performance.component.html',
  styleUrls: ['./performance.component.css']
})
export class PerformanceComponent implements OnInit {
  selectedDataset: Dataset;
  @Input() performanceUris: string[];
  performances: PerformanceModel[];
  @Input() context: SeObjectModel;
  selectedPerformance: PerformanceModel;

  constructor(private _datasetService: DatasetService, public _performanceService: PerformanceService) {
    console.log('Performance component created');
  }

  ngOnInit() {
    this._datasetService.selectedDatasetUpdated.subscribe((dataset) => {
        this.selectedDataset = dataset;
        this._performanceService.loadObjects(this.selectedDataset);
      }
    );
    this.selectedDataset = this._datasetService.selectedDataset;

    this._performanceService.seObjectsUpdated.subscribe((performances) => {
      this.performances = performances;
    });

    if (!this.context) {
      this.performances = this._performanceService.performances;
      this.selectedPerformance = this._performanceService.selectedPerformance;
    } else {
      if (this.performanceUris) {
        this.performances = [];
        for (let index = 0; index < this.performanceUris.length; index++) {
          this.performances.push(this._performanceService.getSeObject(this.performanceUris[index]));
        }
      }
    }
  }

  onSelectedPerformanceChanged(seObject: SeObjectModel): void {
    this.selectedPerformance = <PerformanceModel>seObject;
    this._performanceService.selectPerformance(this.selectedPerformance);
    console.log(this.selectedPerformance.uri);
  }

  createPerformance(): void {
    this._performanceService.createObject();
  }

  getPerformanceLabel(uri: string): string {
    if (Boolean(uri)) {
      return this._performanceService.getSeObject(uri).label;
    } else {
      return '';
    }
  }


}
