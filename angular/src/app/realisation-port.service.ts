import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Dataset} from './dataset/dataset.model';
import {SeObjectService} from './se-object.service';
import {SeObjectModel} from './models/se-object.model';
import {DatasetService} from './dataset.service';
import {RequirementModel} from './models/requirement.model';
import {SeObjectType} from './se-object-type';
import {RealisationPortModel} from './models/realisation-port.model';

@Injectable()
export class RealisationPortService extends SeObjectService {
  apiAddress: string;
  dataset: Dataset;
  realisationPorts: RealisationPortModel[];
  selectedRealisationPort: RealisationPortModel;

  constructor(protected _httpClient: HttpClient, private _datasetService: DatasetService) {
    super(_httpClient);
    this.apiAddress = 'http://localhost:8080/se';
    this._datasetService.selectedDatasetUpdated.subscribe(value => {
      this.dataset = value;
      console.log('RealisationPortService: new dataset: ' + this.dataset.filepath);
      this.selectedRealisationPort = null;
      this.loadObjects(this.dataset);
    });
  }

  loadObjects(dataset: Dataset): void {
    this.load(this.selectedRealisationPort, dataset, SeObjectType.RealisationPortModel).subscribe(value => {
      this.realisationPorts = value;
      console.log('RealisationPortService/loadObjects: ' + this.realisationPorts);
      this.seObjectsUpdated.emit(this.realisationPorts);
    });
  }

  createObject(): void {
    this.create(this.selectedRealisationPort, this.dataset, SeObjectType.RealisationPortModel).subscribe(value => {
      this.realisationPorts.push(value);
      this.seObjectsUpdated.emit(this.realisationPorts);
    });
  }

  updateSeObject(realisationPort: RealisationPortModel): void {
    const hashMark = realisationPort.uri.indexOf('#') + 1;
    const localName = realisationPort.uri.substring(hashMark);
    const request = this.apiAddress + '/datasets/' + this.dataset.id + '/realisation-ports/' + localName;
    this._httpClient.put(request, realisationPort).subscribe(value => {
      console.log('Assembly: ' + (<RealisationPortModel>value).assembly);
    }, error => {
    }, () => {
      console.log('Put operation ready');
    });
  }

  selectRealisationPort(selectedRealisationPort: RealisationPortModel) {
    this.selectedRealisationPort = selectedRealisationPort;
  }

  getSeObject(realisationPortUri: string): RealisationPortModel {
    if (this.realisationPorts) {
      for (let index = 0; index < this.realisationPorts.length; index++) {
        if (this.realisationPorts[index].uri === realisationPortUri) {
          return this.realisationPorts[index];
        }
      }
    }
    return null;
  }

  getSeObjectLabel(realisationPortUri: string): string {
    return this.getSeObject(realisationPortUri).label;
  }

  getSeObjects(): SeObjectModel[] {
    return this.realisationPorts;
  }
}
