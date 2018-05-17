import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Dataset} from './dataset/dataset.model';
import {SeObjectService} from './se-object.service';
import {SeObjectModel} from './models/se-object.model';
import {DatasetService} from './dataset.service';
import {SeObjectType} from './se-object-type';
import {HamburgerModel} from './models/hamburger.model';
import {PortRealisationModel} from './models/port-realisation.model';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class HamburgerService extends SeObjectService {
  apiAddress: string;
  dataset: Dataset;
  hamburgers: HamburgerModel[];
  selectedHamburger: HamburgerModel;

  constructor(protected _httpClient: HttpClient, private _datasetService: DatasetService) {
    super(_httpClient);
    this.apiAddress = 'http://localhost:8080/se';
    this._datasetService.selectedDatasetUpdated.subscribe(value => {
      this.dataset = value;
      console.log('HamburgerService: new dataset: ' + this.dataset.filepath);
      this.selectedHamburger = null;
      this.loadObjects(this.dataset);
    });
  }

  loadObjects(dataset: Dataset): void {
    this.load(this.selectedHamburger, dataset, SeObjectType.HamburgerModel).subscribe(value => {
      this.hamburgers = value;
      this.seObjectsUpdated.emit(this.hamburgers);
    });
  }

  createObject(): void {
    this.create(this.selectedHamburger, this.dataset, SeObjectType.HamburgerModel).subscribe(value => {
      this.hamburgers.push(value);
      this.seObjectsUpdated.emit(this.hamburgers);
    });
  }

  updateSeObject(hamburger: HamburgerModel): void {
    const hashMark = hamburger.uri.indexOf('#') + 1;
    const localName = hamburger.uri.substring(hashMark);
    const request = this.apiAddress + '/datasets/' + this.dataset.id + '/hamburgers/' + localName;
    this._httpClient.put(request, hamburger).subscribe(value => {
      console.log('Assembly: ' + (<HamburgerModel>value).assembly);
    }, error => {
    }, () => {
      console.log('Put operation ready');
    });
  }

  selectHamburger(selectedHamburger: HamburgerModel) {
    this.selectedHamburger = selectedHamburger;
  }

  getSeObject(hamburgerUri: string): HamburgerModel {
    if (this.hamburgers) {
      for (let index = 0; index < this.hamburgers.length; index++) {
        if (this.hamburgers[index].uri === hamburgerUri) {
          return this.hamburgers[index];
        }
      }
    }
    console.log('getSeObject5 ' + hamburgerUri + ' / ' + this.hamburgers);
    return null;
  }

  getSeObjectLabel(hamburgerUri: string): string {
    return this.getSeObject(hamburgerUri).label;
  }

  getSeObjects(): SeObjectModel[] {
    return this.hamburgers;
  }

  getPortRealisations(hamburger: HamburgerModel): Observable<Array<PortRealisationModel>> {
    const hashMark = hamburger.uri.indexOf('#') + 1;
    const localName = hamburger.uri.substring(hashMark);
    const request = this.apiAddress + '/datasets/' + this.dataset.id + '/hamburgers/' + localName + '/port-realisations';
    return this._httpClient.get<Array<PortRealisationModel>>(request);
  }
}
