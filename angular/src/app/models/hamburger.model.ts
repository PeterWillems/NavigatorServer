import {SeObjectModel} from './se-object.model';

export class HamburgerModel extends SeObjectModel {
  functionalUnit: string;
  technicalSolution: string;

  getQueryTag(): string {
    return '/hamburgers';
  }
}
