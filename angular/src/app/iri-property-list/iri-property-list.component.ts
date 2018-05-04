import {Component, Input, OnInit} from '@angular/core';
import {SeObjectModel} from '../se-objectslist/se-object.model';

@Component({
  selector: 'app-iri-property-list',
  templateUrl: './iri-property-list.component.html',
  styleUrls: ['./iri-property-list.component.css']
})
export class IriPropertyListComponent implements OnInit {
  @Input() name: string;
  @Input() items: SeObjectModel[];
  editMode = false;

  constructor() {
  }

  ngOnInit() {
  }

  getLabels(): string[] {
    const labels = [];
    if (this.items) {
      for (let index = 0; index < this.items.length; index++) {
        labels.push(this.items[index].label);
      }
    }
    return labels;
  }

}
