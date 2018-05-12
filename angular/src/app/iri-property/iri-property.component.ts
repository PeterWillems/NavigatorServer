import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {SeObjectType} from '../se-object-type';
import {SystemSlotService} from '../system-slot.service';
import {FunctionService} from '../function.service';
import {SeObjectModel} from '../models/se-object.model';
import {NetworkConnectionService} from '../network-connection.service';
import {RequirementService} from '../requirement.service';
import {NetworkConnectionModel} from '../models/network-connection.model';
import {PerformanceService} from '../performance.service';
import {RealisationModuleService} from '../realisation-module.service';

@Component({
  selector: 'app-iri-property',
  templateUrl: './iri-property.component.html',
  styleUrls: ['./iri-property.component.css']
})
export class IriPropertyComponent implements OnInit, OnChanges {
  @Input() name: string;
  @Input() seObject: SeObjectModel;
  @Input() seObjectType: SeObjectType;
  @Input() disabled: boolean;
  @Output() valueChanged = new EventEmitter();
  editMode = false;
  iri: string;
  label: string;
  options: SeObjectModel[];

  constructor(private _functionService: FunctionService,
              private _networkConnectionService: NetworkConnectionService,
              private _performanceService: PerformanceService,
              private _requirementService: RequirementService,
              private _systemSlotService: SystemSlotService,
              private _realisationModuleService: RealisationModuleService) {
    console.log('IriPropertyComponent created ', this.seObjectType);
  }

  ngOnInit() {
    console.log('IriPropertyComponent initialised ', this.seObjectType);
    console.log('ngOnInit: this.seObject: ' + this.seObject);
    this._reset();
    console.log('After _reset');
  }

  onChange(seObject: SeObjectModel): void {
    console.log('onChange seObject: ' + seObject);
    this.valueChanged.emit(seObject);
  }

  ngOnChanges(changes: SimpleChanges): void {
    const seObjectChanged = changes['seObject'];
    if (seObjectChanged) {
      console.log('ngOnChanges: this.seObject: ' + this.seObject);
      this._reset();
    }
  }

  private _reset(): void {
    console.log('_reset: this.seObject: ' + this.seObject);
    this.iri = this.seObject ? this.seObject.uri : null;
    this.label = this.seObject ? this.seObject.label : '';
    console.log('seObjectType' + this.seObjectType + ' ' + (+this.seObjectType));
    switch (+this.seObjectType) {
      case SeObjectType.FunctionModel:
        console.log('function options');
        this.options = this._functionService.functions;
        break;

      case SeObjectType.NetworkConnectionModel:
        console.log('network-connection options');
        this.options = this._networkConnectionService.networkConnections;
        const systemSlot0 = this.seObject ? (<NetworkConnectionModel>this.seObject).systemSlot0 : null;
        const label0 = systemSlot0 ? this._systemSlotService.getSeObjectLabel(systemSlot0) : '<null>';
        const systemSlot1 = this.seObject ? (<NetworkConnectionModel>this.seObject).systemSlot1 : null;
        const label1 = systemSlot1 ? this._systemSlotService.getSeObjectLabel(systemSlot1) : '<null>';
        this.label = (this.seObject ? this.seObject.label : '') + ' (' + label0 + ' | ' + label1 + ')';
        console.log('NetworkConnection label: ' + this.label + ' ' + label0 + ' ' + label1);
        break;


      case SeObjectType.SystemSlotModel:
        console.log('system-slot options');
        this.options = this._systemSlotService.systemSlots;
        break;

      case SeObjectType.PerformanceModel:
        console.log('performance options');
        this.options = this._performanceService.performances;
        break;

      case SeObjectType.RealisationModuleModel:
        console.log('realisation module options');
        this.options = this._realisationModuleService.realisationModules;
        break;

      case SeObjectType.RequirementModel:
        console.log('requirement options');
        this.options = this._requirementService.requirements;
        break;

      default:
        console.log('undefined options');
        break;
    }
  }

}
