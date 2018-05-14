import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Dataset} from './dataset/dataset.model';
import {SeObjectService} from './se-object.service';
import {SystemInterfaceModel} from './models/system-interface.model';
import {DatasetService} from './dataset.service';
import {SeObjectType} from './se-object-type';

@Injectable()
export class SystemInterfaceService extends SeObjectService {
  apiAddress: string;
  dataset: Dataset;
  systemInterfaces: SystemInterfaceModel[];
  selectedSystemInterface: SystemInterfaceModel;

  constructor(protected _httpClient: HttpClient, private _datasetService: DatasetService) {
    super(_httpClient);
    this.apiAddress = 'http://localhost:8080/se';
    this._datasetService.selectedDatasetUpdated.subscribe(value => {
      this.dataset = value;
      console.log('SystemInterfaceService: new dataset: ' + this.dataset.filepath);
      this.selectedSystemInterface = null;
      this.loadObjects(this.dataset);
    });
  }

  loadObjects(dataset: Dataset): void {
    this.load(this.selectedSystemInterface, dataset, SeObjectType.SystemInterfaceModel).subscribe(value => {
      this.systemInterfaces = value;
      this.seObjectsUpdated.emit(this.systemInterfaces);
    });
  }

  createObject(): void {
    this.create(this.selectedSystemInterface, this.dataset, SeObjectType.SystemInterfaceModel).subscribe(value => {
      this.systemInterfaces.push(value);
      this.seObjectsUpdated.emit(this.systemInterfaces);
    });
  }

  updateSeObject(systemInterface: SystemInterfaceModel): void {
    const hashMark = systemInterface.uri.indexOf('#') + 1;
    const localName = systemInterface.uri.substring(hashMark);
    const request = this.apiAddress + '/datasets/' + this.dataset.id + '/system-interfaces/' + localName;
    this._httpClient.put(request, systemInterface).subscribe(value => {
      console.log('Assembly: ' + (<SystemInterfaceModel>value).assembly);
    }, error => {
    }, () => {
      console.log('Put operation ready');
    });
  }

  selectNetworkCollection(selectedSystemInterface: SystemInterfaceModel) {
    this.selectedSystemInterface = selectedSystemInterface;
  }

  getSeObject(systemInterfaceUri: string): SystemInterfaceModel {
    if (this.systemInterfaces) {
      for (let index = 0; index < this.systemInterfaces.length; index++) {
        if (this.systemInterfaces[index].uri === systemInterfaceUri) {
          return this.systemInterfaces[index];
        }
      }
    }
    console.log('getSeObject: SystemInterface NOT found!');
    return null;
  }

  getSeObjectLabel(systemInterfaceUri: string): string {
    return this.getSeObject(systemInterfaceUri).label;
  }

  getSeObjects(): SystemInterfaceModel[] {
    return this.systemInterfaces;
  }
}
