import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';
import {SeObjectModel} from '../models/se-object.model';
import {SeObjectService} from '../se-object.service';

@Component({
  selector: 'app-se-objectslist',
  templateUrl: './se-objectslist.component.html',
  styleUrls: ['./se-objectslist.component.css']
})
export class SeObjectslistComponent implements OnInit {
  @Input() seObjectType: string;
  @Input() seObjects: SeObjectModel[];
  @Input() selectedSeObject: SeObjectModel;
  @Input() seObjectService: SeObjectService;
  @Output() selectedSeObjectChanged: EventEmitter<SeObjectModel> = new EventEmitter<SeObjectModel>();
  @Input() global = true;
  @Output() objectCreated: EventEmitter<SeObjectModel> = new EventEmitter<SeObjectModel>();

  constructor() {
  }

  ngOnInit() {
    this.seObjectService.seObjectsUpdated.subscribe((seObjects) => {
      if (this.global) {
        this.seObjects = seObjects;
      } else {
        this.seObjects.push(this.seObjectService._createdObject);
        this.objectCreated.emit(this.seObjectService._createdObject);
      }
    });
  }

  onClick(seObject: SeObjectModel): void {
    this.selectedSeObject = seObject;
    this.selectedSeObjectChanged.emit(this.selectedSeObject);
  }

  getSeObjectLabel(seObjectUri: string): string {
    return this.seObjectService.getSeObjectLabel(seObjectUri);
  }

  createObject(): void {
    this.seObjectService.createObject();
  }

}
