import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';

@Component({
  selector: 'app-literal-property',
  templateUrl: './literal-property.component.html',
  styleUrls: ['./literal-property.component.css']
})
export class LiteralPropertyComponent implements OnInit {
  @Input() name: string;
  @Input() value: any;
  @Input() valueType: string;
  @Input() disabled: boolean;
  @Output() valueChanged = new EventEmitter();
  editMode = false;

  constructor() {
  }

  ngOnInit() {
    console.log('name: ' + this.name);
    console.log('value: ' + this.value);
    console.log('valueType: ' + this.valueType);
    console.log('disabled: ' + this.disabled);
  }

  onChange(): void {
    this.valueChanged.emit(this.value);
  }

  // ngOnChanges(changes: SimpleChanges): void {
  //   const seValueChanged = changes['value'];
  //   if (seValueChanged) {
  //     this.valueChanged.emit(this.value);
  //   }
  // }
}
