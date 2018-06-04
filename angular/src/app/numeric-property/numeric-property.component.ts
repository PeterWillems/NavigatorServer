import {Component, Input, OnInit} from '@angular/core';
import {NumericPropertyService} from '../numeric-property.service';
import {NumericPropertyModel} from '../models/numeric-property.model';
import {DatasetService} from '../dataset.service';
import {Dataset} from '../dataset/dataset.model';
import {SeObjectModel} from '../models/se-object.model';
import {PerformanceModel} from '../models/performance.model';

@Component({
  selector: 'app-numeric-property',
  templateUrl: './numeric-property.component.html',
  styleUrls: ['./numeric-property.component.css']
})
export class NumericPropertyComponent implements OnInit {
  selectedDataset: Dataset;
  @Input() numericPropertyUris: string[];
  numericProperties: NumericPropertyModel[];
  selectedNumericProperty: NumericPropertyModel;
  @Input() context: SeObjectModel;

  constructor(private _datasetService: DatasetService,
              public _numericPropertyService: NumericPropertyService) {
    console.log('Numeric property component created');
  }

  ngOnInit() {
    this._datasetService.selectedDatasetUpdated.subscribe((dataset) => {
        this.selectedDataset = dataset;
        this._numericPropertyService.loadObjects(this.selectedDataset);
      }
    );
    this.selectedDataset = this._datasetService.selectedDataset;

    this._numericPropertyService.seObjectsUpdated.subscribe((numericProperties) => {
      this.numericProperties = numericProperties;
    });

    if (!this.context) {
      this.numericProperties = this._numericPropertyService.numericProperties;
      this.selectedNumericProperty = this._numericPropertyService.selectedNumericProperty;
    } else {
      if (this.numericPropertyUris) {
        this.numericProperties = [];
        for (let index = 0; index < this.numericPropertyUris.length; index++) {
          this.numericProperties.push(this._numericPropertyService.getSeObject(this.numericPropertyUris[index]));
        }
      }
    }
  }

  onSelectedNumericPropertyChanged(seObject: SeObjectModel): void {
    this.selectedNumericProperty = <NumericPropertyModel>seObject;
    this._numericPropertyService.selectNumericProperty(this.selectedNumericProperty);
    console.log(this.selectedNumericProperty.uri);
  }
}
