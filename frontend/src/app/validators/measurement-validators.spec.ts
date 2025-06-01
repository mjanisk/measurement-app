import { PATIENT_ID_VALIDATORS, RESULT_VALIDATORS } from './measurement-validators'
import { FormControl } from '@angular/forms'

describe('Measurement Validators', () => {
    it('should validate patient ID as required, positive number, and at most 18 characters', () => {
        const control = new FormControl('', PATIENT_ID_VALIDATORS)
        expect(control.valid).toBeFalse()

        control.setValue('123')
        expect(control.valid).toBeTrue()

        control.setValue('-123')
        expect(control.valid).toBeFalse()

        control.setValue('123456789012345678')
        expect(control.valid).toBeTrue()

        control.setValue('1234567890123456789')
        expect(control.valid).toBeFalse()
    })

    it('should validate result as required and within range (50 to 100)', () => {
        const control = new FormControl('', RESULT_VALIDATORS)
        expect(control.valid).toBeFalse() 

        control.setValue('50.534')
        expect(control.valid).toBeTrue() 

        control.setValue('100.0')
        expect(control.valid).toBeTrue() 

        control.setValue('49.9999')
        expect(control.valid).toBeFalse() 

        control.setValue('101')
        expect(control.valid).toBeFalse() 

        control.setValue('75.423423423')
        expect(control.valid).toBeTrue() 

    })
})