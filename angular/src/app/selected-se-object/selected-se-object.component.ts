import {Component, OnInit, Input} from '@angular/core';
import {SeObjectModel} from '../models/se-object.model';
import {SeObjectService} from '../se-object.service';

@Component({
  selector: 'app-selected-se-object',
  templateUrl: './selected-se-object.component.html',
  styleUrls: ['./selected-se-object.component.css']
})
export class SelectedSeObjectComponent implements OnInit {
  isOpen = false;
  @Input() selectedSeObject: SeObjectModel;
  @Input() seObjectService: SeObjectService;
  seObjects: SeObjectModel[];

  constructor() {
  }

  ngOnInit() {
    this.seObjects = this.seObjectService.getSeObjects();
  }

  getSeObject(selectedSeObjectUri: string): SeObjectModel {
    return this.seObjectService.getSeObject(selectedSeObjectUri);
  }

}
