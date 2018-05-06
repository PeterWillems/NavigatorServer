import {Component, OnInit, OnChanges, SimpleChanges} from '@angular/core';
import {SystemSlotModel} from '../models/system-slot.model';
import {SystemSlotService} from '../system-slot.service';
import {Dataset} from '../dataset/dataset.model';
import {DatasetService} from '../dataset.service';
import {SeObjectModel} from '../models/se-object.model';

@Component({
  selector: 'app-system-slot',
  templateUrl: './system-slot.component.html',
  styleUrls: ['./system-slot.component.css']
})
export class SystemSlotComponent implements OnInit, OnChanges {
  selectedDataset: Dataset;
  systemSlots: SystemSlotModel[];
  selectedSystemSlot: SystemSlotModel;

  constructor(public _systemSlotService: SystemSlotService, private _datasetService: DatasetService) {
    console.log('SystemSlot component created');
  }

  ngOnInit() {
    this._datasetService.selectedDatasetUpdated.subscribe((dataset) => {
        this.selectedDataset = dataset;
        this._systemSlotService.loadSystemSlots(this.selectedDataset);
      }
    );
    this.selectedDataset = this._datasetService.selectedDataset;

    this._systemSlotService.seObjectsUpdated.subscribe((systemSlots) => {
      this.systemSlots = systemSlots;
    });

    if (this.selectedDataset) {
      this._systemSlotService.loadSystemSlots(this.selectedDataset);
    }

    this.selectedSystemSlot = this._systemSlotService.selectedSystemSlot;
  }

  ngOnChanges(changes: SimpleChanges): void {
    const dataset = changes.selectedDataset.currentValue;
    console.log('dataset: ' + dataset ? dataset.toString() : '');
    this._systemSlotService.loadSystemSlots(dataset);
  }

  getSystemSlotLabel(uri: string): string {
    if (Boolean(uri)) {
      return this._systemSlotService.getSeObject(uri).label;
    } else {
      return '';
    }
  }

  onSelectedSystemSlotChanged(seObject: SeObjectModel): void {
    this.selectedSystemSlot = <SystemSlotModel>seObject;
    this._systemSlotService.selectSystemSlot(this.selectedSystemSlot);
    console.log(this.selectedSystemSlot.uri);
  }


}
