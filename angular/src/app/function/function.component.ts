import {Component, OnInit} from '@angular/core';
import {DatasetService} from '../dataset.service';
import {Dataset} from '../dataset/dataset.model';
import {FunctionService} from '../function.service';
import {FunctionModel} from './function.model';
import {SeObjectModel} from '../se-objectslist/se-object.model';

@Component({
  selector: 'app-function',
  templateUrl: './function.component.html',
  styleUrls: ['./function.component.css']
})
export class FunctionComponent implements OnInit {
  selectedDataset: Dataset;
  functions: FunctionModel[];
  selectedFunction: FunctionModel;

  constructor(private _datasetService: DatasetService, public _functionService: FunctionService) {
    console.log('Function component created');
  }

  ngOnInit() {
    this._datasetService.selectedDatasetUpdated.subscribe((dataset) => {
        this.selectedDataset = dataset;
        this._functionService.loadFunctions(this.selectedDataset);
      }
    );
    this.selectedDataset = this._datasetService.selectedDataset;

    this._functionService.seObjectsUpdated.subscribe((functions) => {
      console.log('functions updated!');
      this.functions = functions;
    });

    if (this.selectedDataset) {
      this._functionService.loadFunctions(this.selectedDataset);
    }

    this.selectedFunction = this._functionService.selectedFunction;
  }

  onSelectedFunctionChanged(seObject: SeObjectModel): void {
    this.selectedFunction = <FunctionModel>seObject;
    this._functionService.selectFunction(this.selectedFunction);
    console.log(this.selectedFunction.uri);
  }

  createFunction(): void {
    this._functionService.createSeObject();
  }

  getFunctionLabel(uri: string): string {
    if (Boolean(uri)) {
      return this._functionService.getSeObject(uri).label;
    } else {
      return '';
    }
  }


}
