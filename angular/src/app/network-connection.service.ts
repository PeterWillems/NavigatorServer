import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Dataset} from './dataset/dataset.model';
import {SeObjectService} from './se-object.service';
import {Observable} from 'rxjs/Observable';
import {NetworkConnectionModel} from './models/network-connection.model';
import {DatasetService} from './dataset.service';

@Injectable()
export class NetworkConnectionService extends SeObjectService {
  apiAddress: string;
  dataset: Dataset;
  networkConnections: NetworkConnectionModel[];
  selectedNetworkConnection: NetworkConnectionModel;

  constructor(private _httpClient: HttpClient, private _datasetService: DatasetService) {
    super();
    this.apiAddress = 'http://localhost:8080/se';
    this._datasetService.selectedDatasetUpdated.subscribe(value => {
      this.dataset = value;
      console.log('NetworkConnectionService: new dataset: ' + this.dataset.filepath);
      this.loadNetworkConnections(this.dataset);
    });
  }

  loadNetworkConnections(dataset: Dataset): void {
    this.selectedNetworkConnection = null;
    this.dataset = dataset;
    console.log('loadNetworkConnections: ' + this.dataset.id);
    let networkConnections: Array<NetworkConnectionModel> = [];
    const request = this.apiAddress + '/datasets/' + dataset.id + '/network-connections';
    const networkConnections$ = this._httpClient.get<Array<NetworkConnectionModel>>(request);
    networkConnections$.subscribe(value => {
      networkConnections = value;
    }, error => {
      console.log(error);
    }, () => {
      this.networkConnections = networkConnections;
      this.seObjectsUpdated.emit(networkConnections);
    });
  }

  createSeObject(): void {
    const request = this.apiAddress + '/datasets/' + this.dataset.id + '/network-connections';
    let networkConnection = <NetworkConnectionModel>{uri: 'xxx', label: '', assembly: ''};
    const networkConnection$ = this._httpClient.post<NetworkConnectionModel>(request, networkConnection);
    networkConnection$.subscribe(value => {
      networkConnection = value;
    }, error => {
      console.log(error);
    }, () => {
      this.networkConnections.push(networkConnection);
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

  getSeObjectParts(assembly: NetworkConnectionModel): Observable<NetworkConnectionModel[]> {
    const hashMark = assembly.uri.indexOf('#') + 1;
    const localName = assembly.uri.substring(hashMark);
    const request = this.apiAddress + '/datasets/' + this.dataset.id + '/network-connections/' + localName + '/parts';
    return this._httpClient.get<Array<NetworkConnectionModel>>(request);
  }
}
