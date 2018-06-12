import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {PortRealisationModel} from '../models/port-realisation.model';
import {RealisationPortModel} from '../models/realisation-port.model';
import {PortRealisationService} from '../port-realisation.service';
import {SeObjectModel} from '../models/se-object.model';
import {SeObjectType} from '../se-object-type';
import {RealisationPortService} from '../realisation-port.service';
import {SystemInterfaceModel} from '../models/system-interface.model';
import {SystemInterfaceService} from '../system-interface.service';

@Component({
  selector: 'app-selected-port-realisation',
  templateUrl: './selected-port-realisation.component.html',
  styleUrls: ['./selected-port-realisation.component.css']
})
export class SelectedPortRealisationComponent implements OnInit, OnChanges {
  @Input() selectedPortRealisation: PortRealisationModel;
  portRealisationType = SeObjectType.PortRealisationModel;
  assembly: PortRealisationModel;
  parts: PortRealisationModel[];
  realisationPortType = SeObjectType.RealisationPortModel;
  realisationPort: RealisationPortModel;
  systemInterfaceType = SeObjectType.SystemInterfaceModel;
  systemInterface: SystemInterfaceModel;

  constructor(private _portRealisationService: PortRealisationService,
              private _realisationPortService: RealisationPortService,
              private _systemInterfaceService: SystemInterfaceService) {
  }

  ngOnInit() {
    this._loadStateValues();
  }

  ngOnChanges(changes: SimpleChanges): void {
    const selectedPortRealisationChange = changes['selectedPortRealisation'];
    if (selectedPortRealisationChange) {
      this._loadStateValues();
    }
  }

  private _loadStateValues(): void {
    this.assembly = this.getAssembly();
    this.parts = this.getParts();
    this.realisationPort = this.getRealisationPort();
    this.systemInterface = this.getSystemInterface();
  }

  onLabelChanged(label: string): void {
    this.selectedPortRealisation.label = label;
    this._portRealisationService.updateSeObject(this.selectedPortRealisation);
  }

  getAssembly(): PortRealisationModel {
    if (this.selectedPortRealisation.assembly) {
      return this._portRealisationService.getSeObject(this.selectedPortRealisation.assembly);
    }
    return null;
  }

  onAssemblyChanged(assembly: SeObjectModel): void {
    // update the parts of the previous assembly
    if (this.assembly) {
      for (let index = 0; index < this.assembly.parts.length; index++) {
        if (this.assembly.parts[index] === this.selectedPortRealisation.uri) {
          this.assembly.parts.splice(index, 1);
          break;
        }
      }
      this._portRealisationService.updateSeObject(this.assembly);
    }
    // update the assembly of the selected systemslot
    this.selectedPortRealisation.assembly = assembly ? assembly.uri : null;
    this._portRealisationService.updateSeObject(this.selectedPortRealisation);
    // update the parts of the new assembly if existing
    if (assembly) {
      assembly.parts.push(this.selectedPortRealisation.uri);
      this._portRealisationService.updateSeObject(<PortRealisationModel>assembly);
    }
  }

  getParts(): PortRealisationModel[] {
    const parts = [];
    if (this.selectedPortRealisation.parts) {
      for (let index = 0; index < this.selectedPortRealisation.parts.length; index++) {
        parts.push(this._portRealisationService.getSeObject(this.selectedPortRealisation.parts[index]));
      }
    }
    return parts;
  }

  onRealisationPortChanged(realisationPort: RealisationPortModel): void {
    console.log('SelectedPortRealisationComponent/onRealisationPortChanged: ' + realisationPort);
    this.selectedPortRealisation.realisationPort = realisationPort ? realisationPort.uri : null;
    this._portRealisationService.updateSeObject(this.selectedPortRealisation);
  }

  getRealisationPort(): RealisationPortModel {
    if (this.selectedPortRealisation.realisationPort) {
      return this._realisationPortService.getSeObject(this.selectedPortRealisation.realisationPort);
    }
    return null;
  }

  onSystemInterfaceChanged(systemInterface: SystemInterfaceModel): void {
    console.log('SelectedPortRealisationComponent/onSystemInterfaceChanged: ' + systemInterface);
    this.selectedPortRealisation.systemInterface = systemInterface ? systemInterface.uri : null;
    this._portRealisationService.updateSeObject(this.selectedPortRealisation);
  }

  getSystemInterface(): SystemInterfaceModel {
    if (this.selectedPortRealisation.systemInterface) {
      return this._systemInterfaceService.getSeObject(this.selectedPortRealisation.systemInterface);
    }
    return null;
  }
}
