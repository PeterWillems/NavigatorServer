import {Component, OnInit} from '@angular/core';
import {SystemSlotService} from './system-slot.service';
import {DatasetService} from './dataset.service';
import {FunctionService} from './function.service';
import {Dataset} from './dataset/dataset.model';
import {SystemInterfaceService} from './system-interface.service';
import {RealisationModuleService} from './realisation-module.service';
import {RequirementService} from './requirement.service';
import {PerformanceService} from './performance.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  providers: [
    DatasetService,
    SystemSlotService,
    RealisationModuleService,
    FunctionService,
    PerformanceService,
    RequirementService,
    SystemInterfaceService
  ]
})
export class AppComponent implements OnInit {
  title = 'COINS SE Navigator';
  datasets: Dataset[];
  selectedDataset: Dataset;

  constructor(private _datasetService: DatasetService,
              private _systemSlotService: SystemSlotService,
              private _realisationModuleService: RealisationModuleService,
              private _functionService: FunctionService,
              private _performanceService: PerformanceService,
              private _requirementService: RequirementService,
              private _systemInterfaceService: SystemInterfaceService) {
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
      this._systemSlotService.loadObjects(this.selectedDataset);
      this._realisationModuleService.loadObjects(this.selectedDataset);
      this._functionService.loadObjects(this.selectedDataset);
      this._performanceService.loadObjects(this.selectedDataset);
      this._requirementService.loadObjects(this.selectedDataset);
      this._systemInterfaceService.loadObjects(this.selectedDataset);
    }
  }
}
