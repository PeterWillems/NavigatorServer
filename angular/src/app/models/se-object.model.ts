export abstract class SeObjectModel {
  uri: string;
  label: string;
  assembly: string;
  parts: string;

  abstract getQueryTag(): string;
}
