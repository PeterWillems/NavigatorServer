import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SystemSlotModel} from './models/system-slot.model';
import {Dataset} from './dataset/dataset.model';
import {SeObjectService} from './se-object.service';
import {SeObjectModel} from './models/se-object.model';
import {Observable} from 'rxjs/Observable';
import {FunctionModel} from './models/function.model';
import {DatasetService} from './dataset.service';
import {HamburgerModel} from './models/hamburger.model';
import {SeObjectType} from './se-object-type';

@Injectable()
export class SystemSlotService extends SeObjectService {
  apiAddress: string;
  dataset: Dataset;
  systemSlots: SystemSlotModel[];
  selectedSystemSlot: SystemSlotModel;

  constructor(protected _httpClient: HttpClient, private _datasetService: DatasetService) {
    super(_httpClient);
    this.apiAddress = 'http://localhost:8080/se';
    this._datasetService.selectedDatasetUpdated.subscribe(value => {
      this.dataset = value;
      console.log('SystemSlotService: new dataset: ' + this.dataset.filepath);
      this.selectedSystemSlot = null;
      this.loadObjects(this.dataset);
    });
  }

  loadObjects(dataset: Dataset): void {
    this.load(this.selectedSystemSlot, this.dataset, SeObjectType.SystemSlotModel).subscribe(value => {
      this.systemSlots = value;
      this.seObjectsUpdated.emit(this.systemSlots);
    });
  }

  createObject(): void {
    this.create(this.selectedSystemSlot, this.dataset, SeObjectType.SystemSlotModel).subscribe(value => {
      this.systemSlots.push(value);
      this.seObjectsUpdated.emit(this.systemSlots);
    });
  }

  updateSeObject(systemSlot: SystemSlotModel): void {
    const hashMark = systemSlot.uri.indexOf('#') + 1;
    const localName = systemSlot.uri.substring(hashMark);
    console.log('update: ' + this.dataset.id + ' system slot: ' + localName
      + ' assembly: ' + systemSlot.assembly + ' parts: ' + systemSlot.parts + ' functions: ' + systemSlot.functions);
    const request = this.apiAddress + '/datasets/' + this.dataset.id + '/system-slots/' + localName;
    this._httpClient.put<SystemSlotModel>(request, systemSlot).subscribe(value => {
      systemSlot = value;
      this.seObjectsUpdated.emit(this.systemSlots);
    }, error => {
    }, () => {
    });
  }

  getSeObject(systemSlotUri: string): SystemSlotModel {
    if (this.systemSlots) {
      for (let index = 0; index < this.systemSlots.length; index++) {
        if (this.systemSlots[index].uri === systemSlotUri) {
          return this.systemSlots[index];
        }
      }
    }
    console.log('getSeObject5 ' + systemSlotUri + ' / ' + this.systemSlots);
    return null;
  }

  selectSystemSlot(selectedSystemSlot: SystemSlotModel) {
    this.selectedSystemSlot = selectedSystemSlot;
  }


  getSeObjectLabel(systemSlotUri: string): string {
    return this.getSeObject(systemSlotUri).label;
  }

  getSeObjects(): SeObjectModel[] {
    return this.systemSlots;
  }

  getHamburgers(systemSlotUri: string): Observable<Array<HamburgerModel>> {
    const hashMark = systemSlotUri.indexOf('#') + 1;
    const localName = systemSlotUri.substring(hashMark);
    const request = this.apiAddress + '/datasets/' + this.dataset.id + '/system-slots/' + localName + '/hamburgers';
    return this._httpClient.get<Array<HamburgerModel>>(request);
  }

}
