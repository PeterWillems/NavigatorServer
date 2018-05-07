import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SystemSlotModel} from './models/system-slot.model';
import {Dataset} from './dataset/dataset.model';
import {SeObjectService} from './se-object.service';
import {SeObjectModel} from './models/se-object.model';
import {Observable} from 'rxjs/Observable';
import {FunctionModel} from './models/function.model';
import {DatasetService} from './dataset.service';

@Injectable()
export class SystemSlotService extends SeObjectService {
  apiAddress: string;
  dataset: Dataset;
  systemSlots: SystemSlotModel[];
  selectedSystemSlot: SystemSlotModel;

  constructor(private _httpClient: HttpClient, private _datasetService: DatasetService) {
    super();
    this.apiAddress = 'http://localhost:8080/se';
    this._datasetService.selectedDatasetUpdated.subscribe(value => {
      this.dataset = value;
      console.log('SystemSlotService: new dataset: ' + this.dataset.filepath);
      this.loadSystemSlots(this.dataset);
    });
  }

  loadSystemSlots(dataset: Dataset): void {
    this.selectSystemSlot(null);
    this.dataset = dataset;
    console.log('loadSystemSlots: ' + this.dataset.id);
    let systemSlots: Array<SystemSlotModel> = [];
    const request = this.apiAddress + '/datasets/' + dataset.id + '/system-slots';
    const systemSlots$ = this._httpClient.get<Array<SystemSlotModel>>(request);
    systemSlots$.subscribe(value => {
      systemSlots = value;
    }, error => {
      console.log(error);
    }, () => {
      this.systemSlots = systemSlots;
      this.seObjectsUpdated.emit(systemSlots);
    });
  }

  createSeObject(): void {
    const request = this.apiAddress + '/datasets/' + this.dataset.id + '/system-slots';
    let systemSlot = <SystemSlotModel>{uri: 'xxx', label: '', assembly: ''};
    const systemSlot$ = this._httpClient.post<SystemSlotModel>(request, systemSlot);
    systemSlot$.subscribe(value => {
      systemSlot = value;
    }, error => {
      console.log(error);
    }, () => {
      this.systemSlots.push(systemSlot);
      this.seObjectsUpdated.emit(this.systemSlots);
    });
  }

  updateSeObject(systemSlot: SystemSlotModel): void {
    const hashMark = systemSlot.uri.indexOf('#') + 1;
    const localName = systemSlot.uri.substring(hashMark);
    console.log('update: ' + this.dataset.id + ' system slot: ' + localName + ' assembly: ' + systemSlot.assembly);
    const request = this.apiAddress + '/datasets/' + this.dataset.id + '/system-slots/' + localName;
    this._httpClient.put(request, systemSlot).subscribe(value => {
    }, error => {
    }, () => {
    });
  }

  getSeObject(uri: string): SystemSlotModel {
    for (let index = 0; index < this.systemSlots.length; index++) {
      if (this.systemSlots[index].uri === uri) {
        return this.systemSlots[index];
      }
    }
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
}
