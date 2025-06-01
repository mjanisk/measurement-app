import { SNACKBAR_MESSAGES } from './snackbar-messages'

describe('Snackbar Messages', () => {
    it('should contain a message for form error', () => {
        expect(SNACKBAR_MESSAGES.FORM_ERROR).toBe('Please correct the errors in the form.')
    })

    it('should contain a message for fetch error', () => {
        expect(SNACKBAR_MESSAGES.FETCH_ERROR).toBe('Failed to fetch measurements.')
    })

    it('should contain a message for add success', () => {
        expect(SNACKBAR_MESSAGES.ADD_SUCCESS).toBe('Measurement added successfully.')
    })

    it('should contain a message for update success', () => {
        expect(SNACKBAR_MESSAGES.UPDATE_SUCCESS).toBe('Measurement updated successfully.')
    })

    it('should contain a message for delete success', () => {
        expect(SNACKBAR_MESSAGES.DELETE_SUCCESS).toBe('Measurement deleted successfully.')
    })

    it('should contain a message for unexpected error', () => {
        expect(SNACKBAR_MESSAGES.UNEXPECTED_ERROR).toBe('An unexpected error occurred.')
    })

    it('should contain a message for update error', () => {
        expect(SNACKBAR_MESSAGES.UPDATE_ERROR).toBe('Failed to update the measurement.')
    })

    it('should contain a message for delete error', () => {
        expect(SNACKBAR_MESSAGES.DELETE_ERROR).toBe('Failed to delete the measurement.')
    })
})
