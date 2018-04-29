import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Dataset} from './dataset.model';
import {DatasetService} from '../dataset.service';

@Component({
  selector: 'app-dataset',
  templateUrl: './dataset.component.html',
  styleUrls: ['./dataset.component.css']
})
export class DatasetComponent implements OnInit {
  datasets: Dataset[];
  selectedDataset: Dataset;
  @Output() selectedDatasetChanged: EventEmitter<Dataset> = new EventEmitter<Dataset>();

  constructor(private _datasetService: DatasetService) {
  }

  ngOnInit() {
    this._datasetService.datasetsUpdated.subscribe((datasets) => {
      console.log('datasets updated!');
      this.datasets = datasets;
    });
    this.selectedDataset = this._datasetService.selectedDataset;
    this._datasetService.getDatasets();
  }

  onClick(dataset: Dataset) {
    console.log('onCLick: ' + dataset.filepath);
    this.selectedDataset = dataset;
    this._datasetService.selectDataset(dataset);
  }

}
