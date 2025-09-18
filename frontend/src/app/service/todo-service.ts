import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { PagedResponse } from '../interface/paged-response';
import { Todo } from '../interface/todo';

@Injectable({ providedIn: 'root' })
export class TodoService {
  private baseUrl = '/api/todos';

  constructor(private http: HttpClient) {}

  listTodos(
    page: number,
    size: number,
    sort?: string,
    q?: string,
  ): Observable<PagedResponse<Todo>> {
    let params = new HttpParams().set('page', page).set('size', size);

    if (sort) {
      params = params.set('sort', sort);
    }

    if (q) {
      params = params.set('q', q);
    }

    return this.http
      .get<Todo[]>(this.baseUrl, {
        params,
        observe: 'response',
      })
      .pipe(
        map((response) => {
          const items = response.body ?? [];

          return {
            items,
            page: Number(response.headers.get('X-Page') ?? 0),
            size: Number(response.headers.get('X-Size') ?? items.length),
            totalElements: Number(response.headers.get('X-Total-Elements') ?? items.length),
            totalPages: Number(response.headers.get('X-Total-Pages') ?? 1),
          } as PagedResponse<Todo>;
        }),
      );
  }
}
