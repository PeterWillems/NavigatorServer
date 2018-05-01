import {EventEmitter, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SystemSlotModel} from './system-slot/system-slot.model';
import {Dataset} from './dataset/dataset.model';

@Injectable()
export class SystemSlotService {
  apiAddress: string;
  systemSlotsUpdated = new EventEmitter();
  systemSlots: SystemSlotModel[];
  dataset: Dataset;

  constructor(private _httpClient: HttpClient) {
    this.apiAddress = 'http://localhost:8080/se';
  }

  loadSystemSlots(dataset: Dataset): void {
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
      this.systemSlotsUpdated.emit(systemSlots);
    });
  }

  createSystemSlot(): void {
    const request = this.apiAddress + '/datasets/' + this.dataset.id + '/system-slots';
    let systemSlot = <SystemSlotModel>{uri: 'xxx', label: '', assembly: ''};
    const systemSlot$ = this._httpClient.post<SystemSlotModel>(request, systemSlot);
    systemSlot$.subscribe(value => {
      systemSlot = value;
    }, error => {
      console.log(error);
    }, () => {
      this.systemSlots.push(systemSlot);
      this.systemSlotsUpdated.emit(this.systemSlots);
    });
  }

  update(systemSlot: SystemSlotModel): void {
    const hashMark = systemSlot.uri.indexOf('#') + 1;
    const localName = systemSlot.uri.substring(hashMark);
    console.log('update: ' + this.dataset.id + ' system slot: ' + localName + ' assembly: ' + systemSlot.assembly);
    const request = this.apiAddress + '/datasets/' + this.dataset.id + '/system-slots/' + localName;
    this._httpClient.put(request, systemSlot).subscribe(value => {
      console.log('Assembly: ' + (<SystemSlotModel>value).assembly);
    }, error => {
    }, () => {
      console.log('Put operation ready');
    });
  }

  getSystemSlot(uri: string): SystemSlotModel {
    for (let index = 0; index < this.systemSlots.length; index++) {
      if (this.systemSlots[index].uri === uri) {
        return this.systemSlots[index];
      }
    }
    return null;
  }

}
