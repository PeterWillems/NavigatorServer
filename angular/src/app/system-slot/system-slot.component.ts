import {Component, Input, OnInit, OnChanges, SimpleChanges, EventEmitter, Output} from '@angular/core';
import {SystemSlot} from './system-slot.model';
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
  systemSlots: SystemSlot[];
  selectedSystemSlot: SystemSlot;
  @Output() selectedSystemSlotChanged: EventEmitter<SystemSlot> = new EventEmitter<SystemSlot>();


  constructor(private _systemSlotService: SystemSlotService, private _datasetService: DatasetService) {
  }

  ngOnInit() {
    this._datasetService.selectedDatasetUpdated.subscribe((dataset) => {
        console.log('datasets updated!');
        this.selectedDataset = dataset;
        this._systemSlotService.getSystemSlots(this.selectedDataset);
      }
    );
    this.selectedDataset = this._datasetService.selectedDataset;

    this._systemSlotService.systemSlotsUpdated.subscribe((systemSlots) => {
      console.log('systemSlots updated!');
      this.systemSlots = systemSlots;
    });

    if (this.selectedDataset) {
      this._systemSlotService.getSystemSlots(this.selectedDataset);
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    const dataset = changes.selectedDataset.currentValue;
    console.log('dataset: ' + dataset ? dataset.toString() : '');
    this._systemSlotService.getSystemSlots(dataset);
  }

  onClick(systemSlot: SystemSlot) {
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
