import {EventEmitter, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SystemSlot} from './system-slot/system-slot.model';
import {Dataset} from './dataset/dataset.model';

@Injectable()
export class DatasetService {
  apiAddress: string;
  datasets: Dataset[];
  datasetsUpdated = new EventEmitter();
  selectedDataset: Dataset;
  selectedDatasetUpdated = new EventEmitter();

  constructor(private _httpClient: HttpClient) {
    this.apiAddress = 'http://localhost:8080/se';
  }

  getDatasets(): void {
    let datasets: Array<Dataset> = [];
    const request = this.apiAddress + '/datasets';
    const datasets$ = this._httpClient.get<Array<Dataset>>(request);
    datasets$.subscribe(value => {
      datasets = value;
    }, error => {
      console.log(error);
    }, () => {
      this.datasets = datasets;
      this.datasetsUpdated.emit(datasets);
    });
  }

  selectDataset(dataset: Dataset) {
    this.selectedDataset = dataset;
    this.selectedDatasetUpdated.emit(this.selectedDataset);
  }
}
