import {Component, Input, OnInit} from '@angular/core';
import {DatasetService} from '../dataset.service';
import {Dataset} from '../dataset/dataset.model';
import {FunctionService} from '../function.service';
import {FunctionModel} from '../models/function.model';
import {SeObjectModel} from '../models/se-object.model';

@Component({
  selector: 'app-function',
  templateUrl: './function.component.html',
  styleUrls: ['./function.component.css']
})
export class FunctionComponent implements OnInit {
  selectedDataset: Dataset;
  @Input() functionUris: string[];
  functions: FunctionModel[];
  @Input() context: SeObjectModel;
  selectedFunction: FunctionModel;

  constructor(private _datasetService: DatasetService, public _functionService: FunctionService) {
    console.log('Function component created');
  }

  ngOnInit() {
    this._datasetService.selectedDatasetUpdated.subscribe((dataset) => {
        this.selectedDataset = dataset;
        this._functionService.loadObjects(this.selectedDataset);
      }
    );
    this.selectedDataset = this._datasetService.selectedDataset;

    this._functionService.seObjectsUpdated.subscribe((functions) => {
      this.functions = functions;
    });

    if (!this.context) {
      this.functions = this._functionService.functions;
      this.selectedFunction = this._functionService.selectedFunction;
    } else {
      if (this.functionUris) {
        this.functions = [];
        for (let index = 0; index < this.functionUris.length; index++) {
          this.functions.push(this._functionService.getSeObject(this.functionUris[index]));
        }
      }
    }
  }

  onSelectedFunctionChanged(seObject: SeObjectModel): void {
    this.selectedFunction = <FunctionModel>seObject;
    this._functionService.selectFunction(this.selectedFunction);
    console.log(this.selectedFunction.uri);
  }

  createFunction(): void {
    this._functionService.createObject();
  }

  getFunctionLabel(uri: string): string {
    if (Boolean(uri)) {
      return this._functionService.getSeObject(uri).label;
    } else {
      return '';
    }
  }


}
