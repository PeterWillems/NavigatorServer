import {Component, OnInit, Input} from '@angular/core';
import {FunctionModel} from '../function/function.model';
import {FunctionService} from '../function.service';

@Component({
  selector: 'app-selected-function',
  templateUrl: './selected-function.component.html',
  styleUrls: ['./selected-function.component.css']
})
export class SelectedFunctionComponent implements OnInit {
  @Input() selectedFunction: FunctionModel;

  constructor(public _functionService: FunctionService) {
  }

  ngOnInit() {
  }

  showLabels(requirements: any[]): string {
    return null;
  }

}
