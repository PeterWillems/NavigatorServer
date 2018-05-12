import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Dataset} from './dataset/dataset.model';
import {SeObjectService} from './se-object.service';
import {Observable} from 'rxjs/Observable';
import {NetworkConnectionModel} from './models/network-connection.model';
import {DatasetService} from './dataset.service';
import {SeObjectType} from './se-object-type';

@Injectable()
export class NetworkConnectionService extends SeObjectService {
  apiAddress: string;
  dataset: Dataset;
  networkConnections: NetworkConnectionModel[];
  selectedNetworkConnection: NetworkConnectionModel;

  constructor(protected _httpClient: HttpClient, private _datasetService: DatasetService) {
    super(_httpClient);
    this.apiAddress = 'http://localhost:8080/se';
    this._datasetService.selectedDatasetUpdated.subscribe(value => {
      this.dataset = value;
      console.log('NetworkConnectionService: new dataset: ' + this.dataset.filepath);
      this.selectedNetworkConnection = null;
      this.loadObjects(this.dataset);
    });
  }

  loadObjects(dataset: Dataset): void {
    this.load(this.selectedNetworkConnection, dataset, SeObjectType.NetworkConnectionModel).subscribe(value => {
      this.networkConnections = value;
      this.seObjectsUpdated.emit(this.networkConnections);
    });
  }

  createObject(): void {
    this.create(this.selectedNetworkConnection, this.dataset, SeObjectType.NetworkConnectionModel).subscribe(value => {
      this.networkConnections.push(value);
      this.seObjectsUpdated.emit(this.networkConnections);
    });
  }

  updateSeObject(networkConnection: NetworkConnectionModel): void {
    const hashMark = networkConnection.uri.indexOf('#') + 1;
    const localName = networkConnection.uri.substring(hashMark);
    const request = this.apiAddress + '/datasets/' + this.dataset.id + '/network-connections/' + localName;
    this._httpClient.put(request, networkConnection).subscribe(value => {
      console.log('Assembly: ' + (<NetworkConnectionModel>value).assembly);
    }, error => {
    }, () => {
      console.log('Put operation ready');
    });
  }

  selectNetworkCollection(selectedNetworkConnection: NetworkConnectionModel) {
    this.selectedNetworkConnection = selectedNetworkConnection;
  }

  getSeObject(networkConnectionUri: string): NetworkConnectionModel {
    if (this.networkConnections) {
      for (let index = 0; index < this.networkConnections.length; index++) {
        if (this.networkConnections[index].uri === networkConnectionUri) {
          return this.networkConnections[index];
        }
      }
    }
    console.log('getSeObject: NetworkConnection NOT found!');
    return null;
  }

  getSeObjectLabel(networkConnectionUri: string): string {
    return this.getSeObject(networkConnectionUri).label;
  }

  getSeObjects(): NetworkConnectionModel[] {
    return this.networkConnections;
  }
}
