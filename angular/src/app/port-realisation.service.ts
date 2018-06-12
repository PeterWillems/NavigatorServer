import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Dataset} from './dataset/dataset.model';
import {SeObjectService} from './se-object.service';
import {SeObjectModel} from './models/se-object.model';
import {DatasetService} from './dataset.service';
import {RequirementModel} from './models/requirement.model';
import {SeObjectType} from './se-object-type';
import {RealisationPortModel} from './models/realisation-port.model';
import {PortRealisationModel} from './models/port-realisation.model';

@Injectable()
export class PortRealisationService extends SeObjectService {
  apiAddress: string;
  dataset: Dataset;
  portRealisations: PortRealisationModel[];
  selectedPortRealisation: PortRealisationModel;

  constructor(protected _httpClient: HttpClient, private _datasetService: DatasetService) {
    super(_httpClient);
    this.apiAddress = 'http://localhost:8080/se';
    this._datasetService.selectedDatasetUpdated.subscribe(value => {
      this.dataset = value;
      console.log('PortRealisationService: new dataset: ' + this.dataset.filepath);
      this.selectedPortRealisation = null;
      this.loadObjects(this.dataset);
    });
  }

  loadObjects(dataset: Dataset): void {
    this.load(this.selectedPortRealisation, dataset, SeObjectType.PortRealisationModel).subscribe(value => {
      this.portRealisations = value;
      console.log('PortRealisationService/loadObjects: ' + this.portRealisations);
      this.seObjectsUpdated.emit(this.portRealisations);
    });
  }

  createObject(): void {
    this.create(this.selectedPortRealisation, this.dataset, SeObjectType.PortRealisationModel).subscribe(value => {
      console.log('PortRealisationService/createObject: ' + value.localName);
      this._createdObject = value;
      this.portRealisations.push(<PortRealisationModel>this._createdObject);
      this.seObjectsUpdated.emit(this.portRealisations);
    });
  }

  updateSeObject(portRealisation: PortRealisationModel): void {
    const hashMark = portRealisation.uri.indexOf('#') + 1;
    const localName = portRealisation.uri.substring(hashMark);
    const request = this.apiAddress + '/datasets/' + this.dataset.id + '/port-realisations/' + localName;
    this._httpClient.put(request, portRealisation).subscribe(value => {
      console.log('Assembly: ' + (<PortRealisationModel>value).assembly);
    }, error => {
    }, () => {
      console.log('Put operation ready');
    });
  }

  selectPortRealisation(selectedPortRealisation: PortRealisationModel) {
    this.selectedPortRealisation = selectedPortRealisation;
  }

  getSeObject(portRealisationUri: string): PortRealisationModel {
    if (this.portRealisations) {
      for (let index = 0; index < this.portRealisations.length; index++) {
        if (this.portRealisations[index].uri === portRealisationUri) {
          return this.portRealisations[index];
        }
      }
    }
    return null;
  }

  getSeObjectLabel(portRealisationUri: string): string {
    return this.getSeObject(portRealisationUri).label;
  }

  getSeObjects(): SeObjectModel[] {
    return this.portRealisations;
  }
}
