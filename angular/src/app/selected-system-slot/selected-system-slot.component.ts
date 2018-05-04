import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {SystemSlotModel} from '../system-slot/system-slot.model';
import {SystemSlotService} from '../system-slot.service';
import {FunctionService} from '../function.service';
import {SeObjectType} from '../se-object-type';
import {Observable} from 'rxjs/Observable';
import {SeObjectModel} from '../se-objectslist/se-object.model';

@Component({
  selector: 'app-selected-system-slot',
  templateUrl: './selected-system-slot.component.html',
  styleUrls: ['./selected-system-slot.component.css'],
})
export class SelectedSystemSlotComponent implements OnInit, OnChanges {
  systemSlotType = SeObjectType.SystemSlotModel;
  isOpen = false;
  @Input() selectedSystemSlot: SystemSlotModel;
  parts: SystemSlotModel[];

  constructor(private _systemSlotService: SystemSlotService, private _functionService: FunctionService) {
  }

  ngOnInit() {
    this.getAssembly();
    this.getParts().subscribe(value => this.parts = value);
  }

  getAssembly(): SystemSlotModel {
    if (this.selectedSystemSlot.assembly) {
      return this._systemSlotService.getSeObject(this.selectedSystemSlot.assembly);
    }
    return null;
  }

  getParts(): Observable<SystemSlotModel[]> {
    return this._systemSlotService.getSeObjectParts(this.selectedSystemSlot);
  }

  showLabels(functionUris: string[]): string[] {
    const labels = [];
    if (this._functionService.functions && functionUris) {
      console.log('functionUris: ' + functionUris.toString());
      for (let index = 0; index < functionUris.length; index++) {
        labels.push(this._functionService.getSeObject(functionUris[index]).label);
      }
    }
    return labels;
  }

  getSystemSlot(systemSlotUri: string): SystemSlotModel {
    return systemSlotUri ? this._systemSlotService.getSeObject(systemSlotUri) : null;
  }

  commit(): void {
    this._systemSlotService.updateSeObject(this.selectedSystemSlot);
  }

  ngOnChanges(changes: SimpleChanges): void {
    const selectedSystemSlotChange = changes['selectedSystemSlot'];
    if (selectedSystemSlotChange) {
      this.getParts().subscribe(value => this.parts = value);
    }
  }

  onLabelChanged(label: string): void {
    this.selectedSystemSlot.label = label;
    this._systemSlotService.updateSeObject(this.selectedSystemSlot);
  }

  onAssemblyChanged(assembly: SeObjectModel): void {
    this.selectedSystemSlot.assembly = assembly ? assembly.uri : null;
    this._systemSlotService.updateSeObject(this.selectedSystemSlot);
  }

}
