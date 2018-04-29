import {Component, Input, OnInit} from '@angular/core';
import {SystemSlot} from '../system-slot/system-slot.model';
import {SystemSlotService} from '../system-slot.service';
import {FunctionService} from '../function.service';

@Component({
  selector: 'app-selected-system-slot',
  templateUrl: './selected-system-slot.component.html',
  styleUrls: ['./selected-system-slot.component.css'],
})
export class SelectedSystemSlotComponent implements OnInit {
  @Input() selectedSystemSlot: SystemSlot;
  systemSlots: SystemSlot[];

  constructor(private _systemSlotService: SystemSlotService, private _functionService: FunctionService) {
  }

  ngOnInit() {
    this._systemSlotService.systemSlotsUpdated.subscribe((value) => this.systemSlots = value);
    this.systemSlots = this._systemSlotService.systemSlots;
  }

  showLabels(functionUris: string[]): string[] {
    const labels = [];
    if (this._functionService.functions && functionUris) {
      for (let index = 0; index < functionUris.length; index++) {
        labels.push(this._functionService.getFunction(functionUris[index]).label);
      }
    }
    return labels;
  }

  commit(): void {
    this._systemSlotService.update(this.selectedSystemSlot);
  }

}
