import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {SystemInterfaceService} from '../system-interface.service';
import {RequirementModel} from '../models/requirement.model';
import {SystemInterfaceModel} from '../models/system-interface.model';
import {RequirementService} from '../requirement.service';
import {SystemSlotService} from '../system-slot.service';
import {SeObjectModel} from '../models/se-object.model';
import {SeObjectType} from '../se-object-type';
import {SystemSlotModel} from '../models/system-slot.model';

@Component({
  selector: 'app-selected-system-interface',
  templateUrl: './selected-system-interface.component.html',
  styleUrls: ['./selected-system-interface.component.css']
})
export class SelectedSystemInterfaceComponent implements OnInit, OnChanges {
  systemSlotType = SeObjectType.SystemSlotModel;
  requirementType = SeObjectType.RequirementModel;
  systemInterfaceType = SeObjectType.SystemInterfaceModel;
  isOpen = false;
  @Input() selectedSystemInterface: SystemInterfaceModel;
  assembly: SystemInterfaceModel;
  systemSlot0: SystemSlotModel;
  systemSlot1: SystemSlotModel;
  parts: SystemInterfaceModel[];
  partsEditMode = false;
  requirements: RequirementModel[];
  requirementsEditMode = false;

  constructor(private _systemSlotService: SystemSlotService,
              private _requirementService: RequirementService,
              private _systemInterfaceService: SystemInterfaceService) {
  }

  ngOnInit() {
    this._loadStateValues();
  }

  ngOnChanges(changes: SimpleChanges): void {
    const selectedSystemInterfaceChange = changes['selectedSystemInterface'];
    if (selectedSystemInterfaceChange) {
      this._loadStateValues();
    }
  }

  private _loadStateValues(): void {
    this.assembly = this.getAssembly();
    this.systemSlot0 = this.getSystemSlot(0);
    this.systemSlot1 = this.getSystemSlot(1);
    this.parts = this.getParts();
    this.requirements = this.getRequirements();
  }

  getAssembly(): SystemInterfaceModel {
    if (this.selectedSystemInterface.assembly) {
      return this._systemInterfaceService.getSeObject(this.selectedSystemInterface.assembly);
    }
    return null;
  }

  getParts(): SystemInterfaceModel[] {
    const parts = [];
    if (this.selectedSystemInterface.parts) {
      for (let index = 0; index < this.selectedSystemInterface.parts.length; index++) {
        parts.push(this._systemInterfaceService.getSeObject(this.selectedSystemInterface.parts[index]));
      }
    }
    return parts;
  }

  getSystemSlot(id: number): SystemSlotModel {
    switch (id) {
      case 0:
        if (this.selectedSystemInterface.systemSlot0) {
          return this._systemSlotService.getSeObject(this.selectedSystemInterface.systemSlot0);
        }
        break;
      case 1:
        if (this.selectedSystemInterface.systemSlot1) {
          return this._systemSlotService.getSeObject(this.selectedSystemInterface.systemSlot1);
        }
        break;
    }
    return null;
  }

  getRequirements(): RequirementModel[] {
    const requirements = [];
    if (this.selectedSystemInterface.requirements) {
      for (let index = 0; index < this.selectedSystemInterface.requirements.length; index++) {
        requirements.push(this._requirementService.getSeObject(this.selectedSystemInterface.requirements[index]));
      }
    }
    return requirements;
  }

  onLabelChanged(label: string): void {
    this.selectedSystemInterface.label = label;
    this._systemInterfaceService.updateSeObject(this.selectedSystemInterface);
  }

  onAssemblyChanged(assembly: SeObjectModel): void {
    // update the parts of the previous assembly
    if (this.assembly) {
      for (let index = 0; index < this.assembly.parts.length; index++) {
        if (this.assembly.parts[index] === this.selectedSystemInterface.uri) {
          this.assembly.parts.splice(index, 1);
          break;
        }
      }
      this._systemInterfaceService.updateSeObject(this.assembly);
    }
    // update the assembly of the selected systeminterface
    this.selectedSystemInterface.assembly = assembly ? assembly.uri : null;
    this._systemInterfaceService.updateSeObject(this.selectedSystemInterface);
    // update the parts of the new assembly if existing
    if (assembly) {
      assembly.parts.push(this.selectedSystemInterface.uri);
      this._systemInterfaceService.updateSeObject(<SystemInterfaceModel>assembly);
    }
  }

  onSystemSlotChanged(systemSlot: SystemSlotModel, id: number): void {
    switch (id) {
      case 0:
        // update the parts of the previous slot
        if (this.systemSlot0) {
          for (let index = 0; index < this.systemSlot0.interfaces.length; index++) {
            if (this.systemSlot0.interfaces[index] === this.selectedSystemInterface.uri) {
              this.systemSlot0.interfaces.splice(index, 1);
              break;
            }
          }
          this._systemSlotService.updateSeObject(this.systemSlot0);
        }
        // update the systemslot of the selected systeminterface
        this.selectedSystemInterface.systemSlot0 = systemSlot ? systemSlot.uri : null;
        this._systemInterfaceService.updateSeObject(this.selectedSystemInterface);
        // update the parts of the new assembly if existing
        if (systemSlot) {
          systemSlot.interfaces.push(this.selectedSystemInterface.uri);
          this._systemSlotService.updateSeObject(<SystemSlotModel>systemSlot);
        }
        break;
      case 1:
        // update the parts of the previous slot
        if (this.systemSlot1) {
          for (let index = 0; index < this.systemSlot1.interfaces.length; index++) {
            if (this.systemSlot1.interfaces[index] === this.selectedSystemInterface.uri) {
              this.systemSlot1.interfaces.splice(index, 1);
              break;
            }
          }
          this._systemSlotService.updateSeObject(this.systemSlot1);
        }
        // update the systemslot of the selected systeminterface
        this.selectedSystemInterface.systemSlot1 = systemSlot ? systemSlot.uri : null;
        this._systemInterfaceService.updateSeObject(this.selectedSystemInterface);
        // update the parts of the new assembly if existing
        if (systemSlot) {
          systemSlot.interfaces.push(this.selectedSystemInterface.uri);
          this._systemSlotService.updateSeObject(<SystemSlotModel>systemSlot);
        }
        break;
    }
  }

  onRequirementsEditModeChange(editMode: boolean): void {
    this.requirementsEditMode = editMode;
  }

  onRequirementAdded(): void {
    const newRequirement = new RequirementModel();
    newRequirement.label = '***';
    this.requirements.push(newRequirement);
    console.log('Requirements: ' + this.requirements.toString());
  }

  onRequirementChanged(requirement: RequirementModel, item: RequirementModel): void {
    if (item.label === '***') {
      console.log('***!');
      this.selectedSystemInterface.requirements.push(requirement.uri);
      this._systemInterfaceService.updateSeObject(this.selectedSystemInterface);
      this.requirements = this.getRequirements();
    } else {
      if (requirement === null) {
        for (let index = 0; this.selectedSystemInterface.requirements.length; index++) {
          if (this.selectedSystemInterface.requirements[index] === item.uri) {
            this.selectedSystemInterface.requirements.splice(index, 1);
            break;
          }
        }
        this._systemInterfaceService.updateSeObject(this.selectedSystemInterface);
        this.requirements = this.getRequirements();
      }
    }
  }

  onPartsEditModeChange(editMode: boolean): void {
    console.log('onPartsEditModeChange: ' + editMode);
    this.partsEditMode = editMode;
  }

  onPartAdded(): void {
    const newPart = new SystemInterfaceModel();
    newPart.label = '***';
    this.parts.push(newPart);
    console.log('Parts: ' + this.parts.toString());
  }

  onPartChanged(part: SystemInterfaceModel, item: SystemInterfaceModel): void {
    if (item.label === '***') {
      part.assembly = this.selectedSystemInterface.uri;
      this._systemInterfaceService.updateSeObject(part);
      this.selectedSystemInterface.parts.push(part.uri);
      this._systemInterfaceService.updateSeObject(this.selectedSystemInterface);
      this.parts = this.getParts();
    } else {
      if (part === null) {
        item.assembly = null;
        this._systemInterfaceService.updateSeObject(item);
        for (let index = 0; this.selectedSystemInterface.parts.length; index++) {
          if (this.selectedSystemInterface.parts[index] === item.uri) {
            this.selectedSystemInterface.parts.splice(index, 1);
            break;
          }
        }
        this._systemInterfaceService.updateSeObject(this.selectedSystemInterface);
        this.parts = this.getParts();
      }
    }
  }

}

