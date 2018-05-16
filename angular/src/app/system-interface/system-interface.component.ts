import {Component, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {DatasetService} from '../dataset.service';
import {SeObjectModel} from '../models/se-object.model';
import {Dataset} from '../dataset/dataset.model';
import {SystemInterfaceModel} from '../models/system-interface.model';
import {SystemInterfaceService} from '../system-interface.service';

@Component({
  selector: 'app-system-interface',
  templateUrl: './system-interface.component.html',
  styleUrls: ['./system-interface.component.css']
})
export class SystemInterfaceComponent implements OnInit, OnChanges {
  selectedDataset: Dataset;
  systemInterfaces: SystemInterfaceModel[];
  selectedSystemInterface: SystemInterfaceModel;

  constructor(public _systemInterfaceService: SystemInterfaceService, private _datasetService: DatasetService) {
    console.log('SystemInterface component created');
  }

  ngOnInit() {
    this._datasetService.selectedDatasetUpdated.subscribe((dataset) => {
        this.selectedDataset = dataset;
        this._systemInterfaceService.loadObjects(this.selectedDataset);
      }
    );
    this.selectedDataset = this._datasetService.selectedDataset;

    this._systemInterfaceService.seObjectsUpdated.subscribe((systemInterfaces) => {
      this.systemInterfaces = systemInterfaces;
      this.selectedSystemInterface = this._systemInterfaceService.selectedSystemInterface;
    });

    if (this.selectedDataset) {
      this._systemInterfaceService.loadObjects(this.selectedDataset);
    }

    this.selectedSystemInterface = this._systemInterfaceService.selectedSystemInterface;
  }

  ngOnChanges(changes: SimpleChanges): void {
    const dataset = changes.selectedDataset.currentValue;
    console.log('dataset: ' + dataset ? dataset.toString() : '');
    this._systemInterfaceService.loadObjects(dataset);
  }

  getSystemInterfaceLabel(uri: string): string {
    if (Boolean(uri)) {
      return this._systemInterfaceService.getSeObject(uri).label;
    } else {
      return '';
    }
  }

  onSelectedSystemInterfaceChanged(seObject: SeObjectModel): void {
    this.selectedSystemInterface = <SystemInterfaceModel>seObject;
    this._systemInterfaceService.selectSystemInterface(this.selectedSystemInterface);
    console.log(this.selectedSystemInterface.uri);
  }

}

