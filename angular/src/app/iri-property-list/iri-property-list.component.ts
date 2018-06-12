import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {SeObjectModel} from '../models/se-object.model';
import {SeObjectType} from '../se-object-type';

@Component({
  selector: 'app-iri-property-list',
  templateUrl: './iri-property-list.component.html',
  styleUrls: ['./iri-property-list.component.css']
})
export class IriPropertyListComponent implements OnInit, OnChanges {
  @Input() name: string;
  @Input() items: SeObjectModel[];
  @Input() selectedItemLabel: string;
  @Output() editModeChanged = new EventEmitter();
  @Output() itemAdded = new EventEmitter();
  editMode = false;
  labels: string[];

  constructor() {
  }

  ngOnInit() {
    this.labels = this._getLabels();
  }

  private _getLabels(): string[] {
    const labels = [];
    if (this.items) {
      for (let index = 0; index < this.items.length; index++) {
        labels.push(this.items[index] ? this.items[index].label : '');
      }
    }
    return labels;
  }

  ngOnChanges(changes: SimpleChanges): void {
    const itemsChanged = changes['items'];
    if (itemsChanged) {
      console.log('IriPropertyListComponent/ngOnChanges: ' + this.items);
      this.labels = this._getLabels();
    }
    const selectedItemLabelChanged = changes['selectedItemLabel'];
    if (selectedItemLabelChanged) {
      this.labels = this._getLabels();
    }
  }

  toggleEditMode(): void {
    this.editMode = !this.editMode;
    this.editModeChanged.emit(this.editMode);
  }

  addItem(): void {
    this.editMode = true;
    this.editModeChanged.emit(this.editMode);
    this.itemAdded.emit();
  }

}
