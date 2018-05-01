import {Component, Input, OnInit, OnChanges, SimpleChanges, EventEmitter, Output} from '@angular/core';
import {SystemSlotModel} from './system-slot.model';
import {SystemSlotService} from '../system-slot.service';
import {Dataset} from '../dataset/dataset.model';
import {DatasetService} from '../dataset.service';

@Component({
  selector: 'app-system-slot',
  templateUrl: './system-slot.component.html',
  styleUrls: ['./system-slot.component.css']
})
export class SystemSlotComponent implements OnInit, OnChanges {
  @Input() selectedDataset: Dataset;
  systemSlots: SystemSlotModel[];
  selectedSystemSlot: SystemSlotModel;
  @Output() selectedSystemSlotChanged: EventEmitter<SystemSlotModel> = new EventEmitter<SystemSlotModel>();


  constructor(private _systemSlotService: SystemSlotService, private _datasetService: DatasetService) {
    console.log('SystemSlot component created');
  }

  ngOnInit() {
    this._datasetService.selectedDatasetUpdated.subscribe((dataset) => {
        this.selectedDataset = dataset;
        this._systemSlotService.loadSystemSlots(this.selectedDataset);
      }
    );
    this.selectedDataset = this._datasetService.selectedDataset;

    this._systemSlotService.systemSlotsUpdated.subscribe((systemSlots) => {
      this.systemSlots = systemSlots;
    });

    if (this.selectedDataset) {
      this._systemSlotService.loadSystemSlots(this.selectedDataset);
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    const dataset = changes.selectedDataset.currentValue;
    console.log('dataset: ' + dataset ? dataset.toString() : '');
    this._systemSlotService.loadSystemSlots(dataset);
  }

  onClick(systemSlot: SystemSlotModel) {
    this.selectedSystemSlot = systemSlot;
    this.selectedSystemSlotChanged.emit(this.selectedSystemSlot);
  }

  createSystemSlot(): void {
    this._systemSlotService.createSystemSlot();
  }

  getSystemSlotLabel(uri: string): string {
    if (Boolean(uri)) {
      return this._systemSlotService.getSystemSlot(uri).label;
    } else {
      return '';
    }
  }

}
