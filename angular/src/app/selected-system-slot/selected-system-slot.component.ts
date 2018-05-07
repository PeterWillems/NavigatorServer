import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {SystemSlotModel} from '../models/system-slot.model';
import {SystemSlotService} from '../system-slot.service';
import {FunctionService} from '../function.service';
import {SeObjectType} from '../se-object-type';
import {SeObjectModel} from '../models/se-object.model';
import {FunctionModel} from '../models/function.model';
import {NetworkConnectionModel} from '../models/network-connection.model';
import {NetworkConnectionService} from '../network-connection.service';

@Component({
  selector: 'app-selected-system-slot',
  templateUrl: './selected-system-slot.component.html',
  styleUrls: ['./selected-system-slot.component.css'],
})
export class SelectedSystemSlotComponent implements OnInit, OnChanges {
  systemSlotType = SeObjectType.SystemSlotModel;
  functionType = SeObjectType.FunctionModel;
  networkConnectionType = SeObjectType.NetworkConnectionModel;
  isOpen = false;
  @Input() selectedSystemSlot: SystemSlotModel;
  assembly: SystemSlotModel;
  parts: SystemSlotModel[];
  partsEditMode = false;
  functions: FunctionModel[];
  functionsEditMode = false;
  interfaces: NetworkConnectionModel[];
  interfacesEditMode = false;

  constructor(private _systemSlotService: SystemSlotService,
              private _functionService: FunctionService,
              private _networkConnectionService: NetworkConnectionService) {
  }

  ngOnInit() {
    this._loadStateValues();
  }

  ngOnChanges(changes: SimpleChanges): void {
    const selectedSystemSlotChange = changes['selectedSystemSlot'];
    if (selectedSystemSlotChange) {
      this._loadStateValues();
    }
  }

  private _loadStateValues(): void {
    this.assembly = this.getAssembly();
    this.parts = this.getParts();
    this.functions = this.getFunctions();
    this.interfaces = this.getInterfaces();
  }

  getAssembly(): SystemSlotModel {
    if (this.selectedSystemSlot.assembly) {
      return this._systemSlotService.getSeObject(this.selectedSystemSlot.assembly);
    }
    return null;
  }

  getParts(): SystemSlotModel[] {
    const parts = [];
    if (this.selectedSystemSlot.parts) {
      for (let index = 0; index < this.selectedSystemSlot.parts.length; index++) {
        parts.push(this._systemSlotService.getSeObject(this.selectedSystemSlot.parts[index]));
      }
    }
    return parts;
  }

  getFunctions(): FunctionModel[] {
    const functions = [];
    if (this.selectedSystemSlot.functions) {
      for (let index = 0; index < this.selectedSystemSlot.functions.length; index++) {
        const functionModel = this._functionService.getSeObject(this.selectedSystemSlot.functions[index]);
        functions.push(functionModel);
      }
    }
    return functions;
  }

  getInterfaces(): NetworkConnectionModel[] {
    const interfaces = [];
    if (this.selectedSystemSlot.interfaces) {
      for (let index = 0; index < this.selectedSystemSlot.interfaces.length; index++) {
        const connection = this._networkConnectionService.getSeObject(this.selectedSystemSlot.interfaces[index]);
        interfaces.push(connection);
      }
    }
    return interfaces;
  }

  onLabelChanged(label: string): void {
    this.selectedSystemSlot.label = label;
    this._systemSlotService.updateSeObject(this.selectedSystemSlot);
  }

  onAssemblyChanged(assembly: SeObjectModel): void {
    this.selectedSystemSlot.assembly = assembly ? assembly.uri : null;
    this._systemSlotService.updateSeObject(this.selectedSystemSlot);
  }

  onFunctionsEditModeChange(editMode: boolean): void {
    console.log('onFunctionsEditModeChange: ' + editMode);
    this.functionsEditMode = editMode;
  }

  onPartsEditModeChange(editMode: boolean): void {
    console.log('onPartsEditModeChange: ' + editMode);
    this.partsEditMode = editMode;
  }

  onInterfacesEditModeChange(editMode: boolean): void {
    this.interfacesEditMode = editMode;
  }

}
