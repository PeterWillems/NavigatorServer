import {SeObjectModel} from './se-object.model';

export class RequirementModel extends SeObjectModel {
  getQueryTag(): string {
    return '/requirements';
  }
}
