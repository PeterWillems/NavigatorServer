import {Component, OnInit} from '@angular/core';
import {SystemSlotService} from './system-slot.service';
import {DatasetService} from './dataset.service';
import {FunctionService} from './function.service';
import {Dataset} from './dataset/dataset.model';
import {SystemInterfaceService} from './system-interface.service';
import {RealisationModuleService} from './realisation-module.service';
import {RequirementService} from './requirement.service';
import {PerformanceService} from './performance.service';
import {HamburgerService} from './hamburger.service';
import {RealisationPortService} from './realisation-port.service';
import {NumericPropertyService} from './numeric-property.service';
import {PortRealisationService} from './port-realisation.service';

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
    SystemInterfaceService,
    HamburgerService,
    PortRealisationService,
    RealisationPortService,
    NumericPropertyService
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
              private _systemInterfaceService: SystemInterfaceService,
              private _hamburgerService: HamburgerService,
              private _portRealisationService: PortRealisationService,
              private _realisationPortService: RealisationPortService,
              private _numericPropertyService: NumericPropertyService) {
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
      this._hamburgerService.loadObjects(this.selectedDataset);
      this._portRealisationService.loadObjects(this.selectedDataset);
      this._realisationPortService.loadObjects(this.selectedDataset);
      this._numericPropertyService.loadObjects(this.selectedDataset);
    }
  }
}
