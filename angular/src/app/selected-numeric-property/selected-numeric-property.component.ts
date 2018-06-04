import {Component, Input, OnInit} from '@angular/core';
import {NumericPropertyModel} from '../models/numeric-property.model';
import {NumericPropertyService} from '../numeric-property.service';

@Component({
  selector: 'app-selected-numeric-property',
  templateUrl: './selected-numeric-property.component.html',
  styleUrls: ['./selected-numeric-property.component.css']
})
export class SelectedNumericPropertyComponent implements OnInit {
  @Input() selectedNumericProperty: NumericPropertyModel;

  constructor(private _numericPropertyService: NumericPropertyService) {
  }

  ngOnInit() {
  }

  onLabelChanged(label: string): void {
    this.selectedNumericProperty.label = label;
    this._numericPropertyService.updateSeObject(this.selectedNumericProperty);
  }

  onTypeChanged(type: string): void {
    this.selectedNumericProperty.type = type;
    this._numericPropertyService.updateSeObject(this.selectedNumericProperty);
  }

  onValueChanged(value: any): void {
    console.log('selected-numeric-property onValueChanged: ' + value);
    this.selectedNumericProperty.datatypeValue = value;
    this._numericPropertyService.updateSeObject(this.selectedNumericProperty);
  }

  onUnitChanged(unit: string): void {
    this.selectedNumericProperty.unit = unit;
    this._numericPropertyService.updateSeObject(this.selectedNumericProperty);
  }

}
