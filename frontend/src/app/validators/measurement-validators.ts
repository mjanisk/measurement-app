import { Validators } from '@angular/forms'

export const PATIENT_ID_VALIDATORS = [
    Validators.required,
    Validators.pattern(/^[1-9]\d*$/),
    Validators.maxLength(18)
]

export const RESULT_VALIDATORS = [
    Validators.required,
    Validators.pattern(/^\d+(\.\d+)?$/),
    Validators.min(50.0),
    Validators.max(100.0)   
]
