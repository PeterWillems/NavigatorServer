import {SeObjectModel} from './se-object.model';

export class FunctionModel extends SeObjectModel {
  input: string;
  output: string;
  requirements: string[];

  getQueryTag(): string {
    return '/functions';
  }
}
