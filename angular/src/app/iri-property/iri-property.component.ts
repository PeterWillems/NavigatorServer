import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {SeObjectType} from '../se-object-type';
import {SystemSlotService} from '../system-slot.service';
import {FunctionService} from '../function.service';
import {SeObjectModel} from '../se-objectslist/se-object.model';

@Component({
  selector: 'app-iri-property',
  templateUrl: './iri-property.component.html',
  styleUrls: ['./iri-property.component.css']
})
export class IriPropertyComponent implements OnInit {
  @Input() name: string;
  @Input() seObject: SeObjectModel;
  @Input() seObjectType: SeObjectType;
  @Input() disabled: boolean;
  @Output() valueChanged = new EventEmitter();
  editMode = false;
  iri: string;
  label: string;
  options: SeObjectModel[];

  constructor(private _functionService: FunctionService, private _systemSlotService: SystemSlotService) {
  }

  ngOnInit() {
    this.iri = this.seObject ? this.seObject.uri : null;
    this.label = this.seObject ? this.seObject.label : '';
    if (this.seObjectType) {
      switch (this.seObjectType) {
        case SeObjectType.SystemSlotModel:
          this.options = this._systemSlotService.systemSlots;
          break;
      }
    }
  }

  onChange(seObject: SeObjectModel): void {
    console.log('onChange seObject: ' + seObject);
    this.valueChanged.emit(seObject);
  }

}
