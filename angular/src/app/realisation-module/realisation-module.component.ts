import {Component, OnInit, OnChanges, SimpleChanges} from '@angular/core';
import {SystemSlotModel} from '../models/system-slot.model';
import {SystemSlotService} from '../system-slot.service';
import {Dataset} from '../dataset/dataset.model';
import {DatasetService} from '../dataset.service';
import {SeObjectModel} from '../models/se-object.model';
import {RealisationModuleModel} from '../models/realisation-module.model';
import {RealisationModuleService} from '../realisation-module.service';

@Component({
  selector: 'app-realisation-module',
  templateUrl: './realisation-module.component.html',
  styleUrls: ['./realisation-module.component.css']
})
export class RealisationModuleComponent implements OnInit, OnChanges {
  selectedDataset: Dataset;
  realisationModules: RealisationModuleModel[];
  selectedRealisationModule: RealisationModuleModel;

  constructor(public _realisationModuleService: RealisationModuleService,
              private _datasetService: DatasetService) {
    console.log('RealisationModule component created');
  }

  ngOnInit() {
    this._datasetService.selectedDatasetUpdated.subscribe((dataset) => {
        this.selectedDataset = dataset;
        this._realisationModuleService.loadRealisationModules(this.selectedDataset);
      }
    );
    this.selectedDataset = this._datasetService.selectedDataset;

    this._realisationModuleService.seObjectsUpdated.subscribe((realisationModules) => {
      this.realisationModules = realisationModules;
    });

    if (this.selectedDataset) {
      this._realisationModuleService.loadRealisationModules(this.selectedDataset);
    }

    this.selectedRealisationModule = this._realisationModuleService.selectedRealisationModule;
  }

  ngOnChanges(changes: SimpleChanges): void {
    const dataset = changes.selectedDataset.currentValue;
    console.log('dataset: ' + dataset ? dataset.toString() : '');
    this._realisationModuleService.loadRealisationModules(dataset);
  }

  getSystemSlotLabel(uri: string): string {
    if (Boolean(uri)) {
      return this._realisationModuleService.getSeObject(uri).label;
    } else {
      return '';
    }
  }

  onSelectedSystemSlotChanged(seObject: SeObjectModel): void {
    this.selectedRealisationModule = <RealisationModuleModel>seObject;
    this._realisationModuleService.selectSystemSlot(this.selectedRealisationModule);
    console.log(this.selectedRealisationModule.uri);
  }
}
