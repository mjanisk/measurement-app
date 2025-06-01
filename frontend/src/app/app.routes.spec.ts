import { routes } from './app.routes'

describe('App Routes', () => {
    it('should contain a default route for MeasurementListComponent', () => {
        const defaultRoute = routes.find(route => route.path === '')
        expect(defaultRoute).toBeTruthy()
        expect(defaultRoute?.component?.name).toBe('MeasurementListComponent')
    })
})
