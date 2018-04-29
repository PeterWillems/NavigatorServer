import {Component, OnInit} from '@angular/core';
import {DatasetService} from '../dataset.service';
import {SystemSlotService} from '../system-slot.service';
import {Dataset} from '../dataset/dataset.model';
import {FunctionService} from '../function.service';

@Component({
  selector: 'app-function',
  templateUrl: './function.component.html',
  styleUrls: ['./function.component.css']
})
export class FunctionComponent implements OnInit {
  selectedDataset: Dataset;
  functions: Function[];

  constructor(private _datasetService: DatasetService, private _functionService: FunctionService) {
    console.log('Function component created');
  }

  ngOnInit() {
    this._datasetService.selectedDatasetUpdated.subscribe((dataset) => {
        this.selectedDataset = dataset;
        this._functionService.getFunctions(this.selectedDataset);
      }
    );
    this.selectedDataset = this._datasetService.selectedDataset;

    this._functionService.functionsUpdated.subscribe((functions) => {
      console.log('functions updated!');
      this.functions = functions;
    });

    if (this.selectedDataset) {
      this._functionService.getFunctions(this.selectedDataset);
    }
  }

}
