import {Component, Input, OnInit} from '@angular/core';
import {FunctionService} from '../function.service';
import {Dataset} from '../dataset/dataset.model';
import {SeObjectModel} from '../models/se-object.model';
import {FunctionModel} from '../models/function.model';
import {DatasetService} from '../dataset.service';
import {HamburgerModel} from '../models/hamburger.model';
import {HamburgerService} from '../hamburger.service';

@Component({
  selector: 'app-hamburger',
  templateUrl: './hamburger.component.html',
  styleUrls: ['./hamburger.component.css']
})
export class HamburgerComponent implements OnInit {
  selectedDataset: Dataset;
  @Input() hamburgerUris: string[];
  hamburgers: HamburgerModel[];
  @Input() context: SeObjectModel;
  selectedHamburger: HamburgerModel;

  constructor(private _datasetService: DatasetService, public _hamburgerService: HamburgerService) {
    console.log('Hamburger component created');
  }

  ngOnInit() {
    this._datasetService.selectedDatasetUpdated.subscribe((dataset) => {
        this.selectedDataset = dataset;
        this._hamburgerService.loadObjects(this.selectedDataset);
      }
    );
    this.selectedDataset = this._datasetService.selectedDataset;

    this._hamburgerService.seObjectsUpdated.subscribe((hamburgers) => {
      this.hamburgers = hamburgers;
    });

    if (!this.context) {
      this.hamburgers = this._hamburgerService.hamburgers;
      this.selectedHamburger = this._hamburgerService.selectedHamburger;
    } else {
      if (this.hamburgerUris) {
        this.hamburgers = [];
        for (let index = 0; index < this.hamburgerUris.length; index++) {
          this.hamburgers.push(this._hamburgerService.getSeObject(this.hamburgerUris[index]));
        }
      }
    }
  }

  onSelectedHamburgerChanged(seObject: SeObjectModel): void {
    this.selectedHamburger = <HamburgerModel>seObject;
    this._hamburgerService.selectHamburger(this.selectedHamburger);
    console.log(this.selectedHamburger.uri);
  }

  createHamburger(): void {
    this._hamburgerService.createObject();
  }

  getHamburgerLabel(uri: string): string {
    if (Boolean(uri)) {
      return this._hamburgerService.getSeObject(uri).label;
    } else {
      return '';
    }
  }
}
