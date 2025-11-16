import Lara from '@primeuix/themes/lara';
import {definePreset} from '@primeuix/themes';

export const MyPreset = definePreset(Lara, {
  semantic: {
    colorScheme: {
      light: {
        primary: {
          50: '#f3f3f3',
          100: '#3A3A3A',
          300: '#E51718',
          400: '#4111A4',
          500: '#2F70EF',
          600: '#418fde'
        },
        highlight: {
          background: '{primary.100}',
          focusBackground: '{primary.300}',
          color: '#ffffff',
          focusColor: '#ffffff'
        },
      }
    }
  },
  components: {
    button: {
      colorScheme: {
        light: {
            text: {
              primary: {
                hoverBackground: '{primary.50}'
              }
            }
          }
      }
    },
    menubar: {
      colorScheme: {
        light: {
          root: {
            background: '#ffffff'
          }
        }
      }
    },
    splitter: {
      colorScheme: {
        light: {
          root: {
            background: '#f3f3f3;'
          }
        }
      }
    }
  }
})
