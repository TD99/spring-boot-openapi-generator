import { Component, OnInit } from '@angular/core';
import { Todo } from '../../interface/todo';
import { TodoService } from '../../service/todo-service';
import {
  CdkFixedSizeVirtualScroll,
  CdkVirtualForOf,
  CdkVirtualScrollViewport,
} from '@angular/cdk/scrolling';

@Component({
  selector: 'app-todo-list',
  templateUrl: './todo-list.html',
  styleUrl: './todo-list.css',
  imports: [CdkVirtualScrollViewport, CdkFixedSizeVirtualScroll, CdkVirtualForOf],
})
export class TodoList implements OnInit {
  todos: Todo[] = [];

  page = 0;
  size = 20;
  totalPages = 1;
  loading = false;

  constructor(private todoService: TodoService) {}

  ngOnInit() {
    this.loadPage(0);
  }

  loadPage(page: number) {
    if (this.loading || page >= this.totalPages) {
      return;
    }

    this.loading = true;
    this.todoService.listTodos(page, this.size).subscribe((response) => {
      this.todos = [...this.todos, ...response.items];
      this.page = response.page;
      this.totalPages = response.totalPages;
      this.loading = false;
    });
  }

  onScroll(index: number) {
    if (index + this.size >= this.todos.length && this.page + 1 < this.totalPages) {
      this.loadPage(this.page + 1);
    }
  }
}
