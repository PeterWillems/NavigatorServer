import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Dataset} from './dataset/dataset.model';
import {SeObjectService} from './se-object.service';
import {SeObjectModel} from './models/se-object.model';
import {DatasetService} from './dataset.service';
import {SeObjectType} from './se-object-type';
import {PerformanceModel} from './models/performance.model';

@Injectable()
export class PerformanceService extends SeObjectService {
  apiAddress: string;
  dataset: Dataset;
  performances: PerformanceModel[];
  selectedPerformance: PerformanceModel;

  constructor(protected _httpClient: HttpClient, private _datasetService: DatasetService) {
    super(_httpClient);
    this.apiAddress = 'http://localhost:8080/se';
    this._datasetService.selectedDatasetUpdated.subscribe(value => {
      this.dataset = value;
      console.log('PerformanceService: new dataset: ' + this.dataset.filepath);
      this.selectedPerformance = null;
      this.loadObjects(this.dataset);
    });
  }

  loadObjects(dataset: Dataset): void {
    this.load(this.selectedPerformance, dataset, SeObjectType.PerformanceModel).subscribe(value => {
      this.performances = value;
      this.seObjectsUpdated.emit(this.performances);
    });
  }

  createObject(): void {
    this.create(this.selectedPerformance, this.dataset, SeObjectType.PerformanceModel).subscribe(value => {
      this.performances.push(value);
      this.seObjectsUpdated.emit(this.performances);
    });
  }

  updateSeObject(performance: PerformanceModel): void {
    const hashMark = performance.uri.indexOf('#') + 1;
    const localName = performance.uri.substring(hashMark);
    const request = this.apiAddress + '/datasets/' + this.dataset.id + '/performances/' + localName;
    this._httpClient.put(request, performance).subscribe(value => {
      console.log('Assembly: ' + (<PerformanceModel>value).assembly);
    }, error => {
    }, () => {
      console.log('Put operation ready');
    });
  }

  selectPerformance(selectedPerformance: PerformanceModel) {
    this.selectedPerformance = selectedPerformance;
  }

  getSeObject(performanceUri: string): PerformanceModel {
    console.log('PerformanceService.getSeObject 1');
    if (this.performances) {
      console.log('PerformanceService.getSeObject 2');
      for (let index = 0; index < this.performances.length; index++) {
        console.log('PerformanceService.getSeObject 3');
        if (this.performances[index].uri === performanceUri) {
          console.log('PerformanceService.getSeObject 4');
          return this.performances[index];
        }
      }
    }
    console.log('PerformanceService.getSeObject 5');
    return null;
  }

  getSeObjectLabel(performanceUri: string): string {
    return this.getSeObject(performanceUri).label;
  }

  getSeObjects(): SeObjectModel[] {
    return this.performances;
  }
}
