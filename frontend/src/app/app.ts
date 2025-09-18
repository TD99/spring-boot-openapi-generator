import { Component } from '@angular/core';
import { TodoList } from './component/todo-list/todo-list';

@Component({
  selector: 'app-root',
  template: `<app-todo-list />`,
  imports: [TodoList],
})
export class App {}
