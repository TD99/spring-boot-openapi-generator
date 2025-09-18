/**
 * Represents a generic paged response suitable for paginated API results.
 */
export interface PagedResponse<T> {
    items: T[];
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
}