import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {SeObjectType} from '../se-object-type';
import {SystemSlotService} from '../system-slot.service';
import {FunctionService} from '../function.service';
import {SeObjectModel} from '../models/se-object.model';
import {NetworkConnectionService} from '../network-connection.service';

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
              private _systemSlotService: SystemSlotService) {
    console.log('IriPropertyComponent created ', this.seObjectType);
  }

  ngOnInit() {
    console.log('IriPropertyComponent initialised ', this.seObjectType);
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
      this._reset();
    }
  }

  private _reset(): void {
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
        break;


      case SeObjectType.SystemSlotModel:
        console.log('system-slot options');
        this.options = this._systemSlotService.systemSlots;
        break;

      default:
        console.log('undefined options');
        break;
    }
  }

}
