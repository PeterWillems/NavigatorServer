import {Component, OnInit} from '@angular/core';
import {SystemSlotService} from './system-slot.service';
import {DatasetService} from './dataset.service';
import {FunctionService} from './function.service';
import {Dataset} from './dataset/dataset.model';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  providers: [DatasetService, SystemSlotService, FunctionService]
})
export class AppComponent implements OnInit {
  title = 'COINS SE Navigator';
  datasets: Dataset[];
  selectedDataset: Dataset;

  constructor(private _datasetService: DatasetService,
              private _systemSlotService: SystemSlotService,
              private _functionService: FunctionService) {
  }

  ngOnInit(): void {
    this._datasetService.datasetsUpdated.subscribe((datasets) => {
      this.datasets = datasets;
      this._initialiseServices();
    });
    this._datasetService.getDatasets();
  }

  _initialiseServices(): void {
    if (this.datasets && this.datasets.length > 0) {
      this.selectedDataset = this.datasets[0];
      this._datasetService.selectDataset(this.selectedDataset);
      this._systemSlotService.loadSystemSlots(this.selectedDataset);
      this._functionService.loadFunctions(this.selectedDataset);
    }
  }
}
